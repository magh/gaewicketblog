package org.gaewicketblog.wicket.page;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.gaewicketblog.common.AppEngineHelper;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.CommentHelper;
import org.gaewicketblog.model.TopicSetting;
import org.gaewicketblog.model.TopicSettingHelper;
import org.gaewicketblog.wicket.application.BlogApplication;
import org.gaewicketblog.wicket.common.DisqusCountPanel;
import org.gaewicketblog.wicket.common.SimplePagingNavigator;
import org.gaewicketblog.wicket.panel.TocPanel;
import org.gaewicketblog.wicket.provider.DatabaseCommentProvider;
import org.gaewicketblog.wicket.provider.ICommentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class IssueListPage extends BorderPage {

	private final static Logger log = LoggerFactory.getLogger(IssueListPage.class);

	public IssueListPage() {
		super();
		String path = RequestCycle.get().getRequest().getPath();
		log.debug("path="+path);
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
//	public IssueListPage(TopicSetting setting, List<Comment> comments){
//		super();
//		FixedCommentProvider provider = new FixedCommentProvider(comments);
//		init(setting, provider);
//	}

	private void init(final TopicSetting setting, SortableDataProvider<Comment> provider) {
		log.debug("ListPage<long> "+setting);

		add(new Label("topic", setting.topic));
		add(new Label("topicdesc", setting.topicdesc));

		String adminemail = getString("admin.email");
		boolean admin = AppEngineHelper.isAdmin(adminemail);

		final DataView<Comment> dataView = new DataView<Comment>("sorting", provider) {
			@Override
			protected void populateItem(final Item<Comment> item) {
				final Comment comment = item.getModelObject();
				item.add(new Label("author", comment.getAuthor()));
				item.add(new Label("date", ""+comment.getDate()));
				item.add(new ExternalLink("comments", CommentHelper
						.getUrlPath(comment)+"#disqus_thread"));
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

		dataView.setItemsPerPage(20);
		provider.setSort(ICommentProvider.SORT_DATE, false);

		add(new OrderByBorder("orderBySubject", ICommentProvider.SORT_SUBJECT,
				provider) {
			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		}.setVersioned(false));
		add(new OrderByBorder("orderByAuthor", ICommentProvider.SORT_AUTHOR,
				provider) {
			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		}.setVersioned(false));
		add(new OrderByBorder("orderByDate", ICommentProvider.SORT_DATE,
				provider) {
			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		}.setVersioned(false));

		add(dataView);

		add(new SimplePagingNavigator("navigator", dataView));

		//add
		add(new Link<String>("add") {
			@Override
			public void onClick() {
				setResponsePage(new AddPage(setting, getString("issuelistpage.post")));
			}
		}.setVisible(setting.canPost || admin));
		//disqus/comments
		String shortname = getString("disqus.shortname");
		add(new DisqusCountPanel("disqus", shortname));

		addSidebarPanel(new TocPanel(nextSidebarId(), provider));
	}

}
