package org.gaewicketblog.wicket.page;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.gaewicketblog.common.AppEngineHelper;
import org.gaewicketblog.common.WicketHelper;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.CommentHelper;
import org.gaewicketblog.model.TopicSetting;
import org.gaewicketblog.model.TopicSettingHelper;
import org.gaewicketblog.wicket.application.BlogApplication;
import org.gaewicketblog.wicket.common.DisqusCountPanel;
import org.gaewicketblog.wicket.common.SimplePagingNavigator;
import org.gaewicketblog.wicket.panel.ContactPanel;
import org.gaewicketblog.wicket.panel.RecentPostsPanel;
import org.gaewicketblog.wicket.panel.SearchPanel;
import org.gaewicketblog.wicket.provider.DatabaseCommentProvider;
import org.gaewicketblog.wicket.provider.FixedCommentProvider;
import org.gaewicketblog.wicket.provider.ICommentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ListPage extends BorderPage {

	private final static Logger log = LoggerFactory.getLogger(ListPage.class);

	public ListPage() {
		super();
		String path = WicketHelper.getCurrentRestfulPath();
		BlogApplication app = (BlogApplication) getApplication();
		TopicSetting setting = TopicSettingHelper.getByPath(app.topics, path);
		DatabaseCommentProvider provider = new DatabaseCommentProvider(setting.id);
		init(setting, provider);
	}

	public ListPage(long parentid) {
		super();
		log.debug("parentid="+parentid);
		BlogApplication app = (BlogApplication) getApplication();
		TopicSetting setting = TopicSettingHelper.getById(app.topics, parentid);
		DatabaseCommentProvider provider = new DatabaseCommentProvider(parentid);
		init(setting, provider);
	}

	/**
	 * To display arbitrary comments, e.g. from search.
	 * @param comments
	 */
	public ListPage(TopicSetting setting, List<Comment> comments){
		super();
		FixedCommentProvider provider = new FixedCommentProvider(comments);
		init(setting, provider);
	}

	private void init(final TopicSetting setting, SortableDataProvider<Comment> provider) {
		log.debug("ListPage<long> "+setting);

		add(new Label("topic", setting.topic));
		add(new Label("topicdesc", setting.topicdesc));

		String adminemail = getString("admin.email");
		boolean admin = AppEngineHelper.isCurrentUser(adminemail);

		final DataView<Comment> dataView = new DataView<Comment>("sorting", provider) {
			@Override
			protected void populateItem(final Item<Comment> item) {
				final Comment comment = item.getModelObject();
				item.add(new Label("author", comment.getAuthor()));
				item.add(new MultiLineLabel("text", comment.getText()
						.getValue()).setVisible(setting.showText)
						.setEscapeModelStrings(false));
				// <p><span wicket:id="note" class="triangle-border right">[note]</span></p>
//				String note = comment.getNote() != null ? comment.getNote().getValue() : "";
//				item.add(new ImgSmartLinkMultiLineLabel("note", note).setVisible(
//						!Util.isEmpty(note)).setEscapeModelStrings(false));
				item.add(new Label("date", ""+comment.getDate()));
				item.add(new ExternalLink("comments", CommentHelper
						.getUrlPath(comment)+"#disqus_thread"));
				item.add(new ExternalLink("viewpost", CommentHelper
						.getUrlPath(comment)).add(new Label("subject", comment
						.getSubject())));
			}
		};

		dataView.setItemsPerPage(5);
		provider.setSort(ICommentProvider.SORT_DATE, false);

		add(dataView);

		add(new SimplePagingNavigator("navigator", dataView));

		//add
		final String title = getString("listpage.post");
		add(new Link<String>("add1") {
			@Override
			public void onClick() {
				setResponsePage(new EditPage(title, setting));
			}
		}.setVisible(setting.canPost || admin));
		add(new Link<String>("add2") {
			@Override
			public void onClick() {
				setResponsePage(new EditPage(title, setting));
			}
		}.setVisible(setting.canPost || admin));
		//disqus/comments
		String shortname = getString("disqus.shortname");
		add(new DisqusCountPanel("disqus", shortname));

		addSidebarPanel(new SearchPanel(nextSidebarId()));
		addSidebarPanel(new ContactPanel(nextSidebarId()));
		addSidebarPanel(new RecentPostsPanel(nextSidebarId(), provider));
	}

}
