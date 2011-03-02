package org.gaewicketblog.wicket.page;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.gaewicketblog.common.AppEngineHelper;
import org.gaewicketblog.common.DbHelper;
import org.gaewicketblog.common.WicketHelper;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.CommentHelper;
import org.gaewicketblog.model.TopicSetting;
import org.gaewicketblog.model.TopicSettingHelper;
import org.gaewicketblog.wicket.application.BlogApplication;
import org.gaewicketblog.wicket.common.SimplePagingNavigator;
import org.gaewicketblog.wicket.provider.DatabaseCommentProvider;
import org.gaewicketblog.wicket.provider.FixedCommentProvider;
import org.gaewicketblog.wicket.provider.ICommentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.repackaged.com.google.common.base.Pair;

@SuppressWarnings("serial")
public class IssueListPage extends BorderPage {

	private final static Logger log = LoggerFactory.getLogger(IssueListPage.class);

	private final static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("dd-MMM-yy");

	private final static int STATUSES_OPEN = 1000;
	private final static int STATUSES_CLOSED = 1001;

	public IssueListPage() {
		super();
		String path = WicketHelper.getCurrentRestfulPath();
		BlogApplication app = (BlogApplication) getApplication();
		TopicSetting setting = TopicSettingHelper.getByPath(app.topics, path);
		DatabaseCommentProvider provider = new DatabaseCommentProvider(setting.id);
		init(setting, provider);
	}

//	public IssueListPage(long parentid) {
//		super();
//		log.debug("parentid="+parentid);
//		DatabaseCommentProvider provider = new DatabaseCommentProvider(parentid);
//		BlogApplication app = (BlogApplication) getApplication();
//		TopicSetting setting = TopicSettingHelper.getById(app.topics, parentid);
//		init(setting, provider);
//	}

	/**
	 * To display arbitrary comments, e.g. from search.
	 * @param comments
	 */
	public IssueListPage(TopicSetting setting, List<Comment> comments){
		super();
		FixedCommentProvider provider = new FixedCommentProvider(comments);
		init(setting, provider);
	}

	private void init(final TopicSetting setting, SortableDataProvider<Comment> provider) {
		log.debug("ListPage<long> "+setting);

		add(new Label("topic", setting.topic));
		add(new Link<Void>("topicdescaddlink"){
			@Override
			public void onClick() {
				setResponsePage(new EditPage(getString("issuelistpage.post"),
						setting));
			}
		}.add(new Label("topicdesc", setting.topicdesc)));

		String adminemail = getString("admin.email");
		boolean admin = AppEngineHelper.isCurrentUser(adminemail);

		final DataView<Comment> dataView = new DataView<Comment>("sorting", provider) {
			@Override
			protected void populateItem(final Item<Comment> item) {
				final Comment comment = item.getModelObject();
				Integer status = comment.getStatus();
				item.add(CommentHelper.newStatusColorLabel(this, "status",
						status));
				item.add(new Label("votes", ""+comment.getVotes()));
				item.add(new Label("author", comment.getAuthor()));
				item.add(new Label("date", DATEFORMAT.format(comment.getDate())));
				item.add(new Label("comments", ""+comment.getComments()));
				item.add(new ExternalLink("viewpost", CommentHelper
						.getUrlPath(comment)).add(new Label("subject", comment
						.getSubject())));
				item.add(new AttributeModifier("class", true,
						new AbstractReadOnlyModel<String>() {
							@Override
							public String getObject() {
								return (item.getIndex() % 2 == 1) ? "even"
										: "odd";
							}
						}));
			}
		};

		dataView.setItemsPerPage(15);
		provider.setSort(ICommentProvider.SORT_DATE, false);

		add(newOrderByBorder("orderByStatus", ICommentProvider.SORT_STATUS,
				provider, dataView));
		add(newOrderByBorder("orderBySubject", ICommentProvider.SORT_SUBJECT,
				provider, dataView));
		add(newOrderByBorder("orderByAuthor", ICommentProvider.SORT_AUTHOR,
				provider, dataView));
		add(newOrderByBorder("orderByDate", ICommentProvider.SORT_DATE,
				provider, dataView));
		add(newOrderByBorder("orderByVotes", ICommentProvider.SORT_VOTES,
				provider, dataView));
		add(newOrderByBorder("orderByComments", ICommentProvider.SORT_COMMENTS,
				provider, dataView));

		add(dataView);

		add(new SimplePagingNavigator("navigator", dataView));

		final IModel<String> search = new Model<String>();
		final IModel<Pair<Integer, String>> status = new Model<Pair<Integer, String>>();
		Form<Void> searchform = new Form<Void>("searchform"){
			@Override
			protected void onSubmit() {
				search(setting, search.getObject(), status.getObject());
			}
		};
		add(searchform);
		//add
		searchform.add(new Link<String>("add") {
			@Override
			public void onClick() {
				setResponsePage(new EditPage(getString("issuelistpage.post"),
						setting));
			}
		}.setVisible(setting.canPost || admin));

		List<Pair<Integer, String>> choices = new ArrayList<Pair<Integer,String>>();
		String openStr = getString("comment.status.open");
		choices.add(new Pair<Integer, String>(STATUSES_OPEN, openStr));
		choices.add(newStatusPair(Comment.STATUS_UNASSIGNED));
		choices.add(newStatusPair(Comment.STATUS_OPEN_NEEDSINFO));
		choices.add(newStatusPair(Comment.STATUS_OPEN_UNDERREVIEW));
		choices.add(newStatusPair(Comment.STATUS_OPEN_STARTED));
		choices.add(newStatusPair(Comment.STATUS_OPEN_PENDING));
		String closedStr = getString("comment.status.closed");
		choices.add(new Pair<Integer, String>(STATUSES_CLOSED, closedStr));
		choices.add(newStatusPair(Comment.STATUS_CLOSED_COMPLETED));
		choices.add(newStatusPair(Comment.STATUS_CLOSED_DECLINED));
		choices.add(newStatusPair(Comment.STATUS_CLOSED_DUPLICATE));

		searchform.add(new DropDownChoice<Pair<Integer, String>>("status", status,
				choices, new ChoiceRenderer<Pair<Integer, String>>("second")));
		searchform.add(new TextField<String>("search", search));
	}
	
	private void search(TopicSetting setting, String in,
			Pair<Integer, String> status) {
		List<Comment> comments = DbHelper.getComments(setting.id);
		int[] statusArr = null;
		if(status != null) {
			if(status.first == STATUSES_OPEN){
				statusArr = Comment.STATUSES_OPEN;
			} else if(status.first == STATUSES_CLOSED) {
				statusArr = Comment.STATUSES_CLOSED;
			} else {
				statusArr = new int[]{status.first};
			}
		}
		setResponsePage(new IssueListPage(setting, CommentHelper
				.search(comments, in, statusArr)));
	}

	private Pair<Integer, String> newStatusPair(int status) {
		String statusStr = "-- "+CommentHelper.getStatusString(this, status);
		return new Pair<Integer, String>(status, statusStr);
	}

	private static OrderByBorder newOrderByBorder(String id, String sort,
			SortableDataProvider<Comment> provider,
			final DataView<Comment> dataView) {
		OrderByBorder order = new OrderByBorder(id, sort, provider) {
			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		};
		order.setVersioned(false);
		return order;
	}

}
