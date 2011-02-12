package org.gaewicketblog.wicket;

import java.util.List;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkMultiLineLabel;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.gaewicketblog.common.AppEngineHelper;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.CommentHelper;
import org.gaewicketblog.wicket.common.SimplePagingNavigator;
import org.gaewicketblog.wicket.common.DisqusCountPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ListPage extends BorderPage {

	private final static Logger log = LoggerFactory.getLogger(ListPage.class);

	public ListPage() {
		super();
		String path = RequestCycle.get().getRequest().getPath();
		log.debug("path="+path);
		BlogApplication app = (BlogApplication) getApplication();
		TopicSetting setting = TopicSettingHelper.getByPath(app.topics, path);
		CommentProvider provider = new CommentProvider(setting.id);
		init(setting, provider);
	}

	public ListPage(long parentid) {
		super();
		log.debug("parentid="+parentid);
		CommentProvider provider = new CommentProvider(parentid);
		BlogApplication app = (BlogApplication) getApplication();
		TopicSetting setting = TopicSettingHelper.getById(app.topics, parentid);
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
		boolean admin = AppEngineHelper.isAdmin(adminemail);

		final DataView<Comment> dataView = new DataView<Comment>("sorting", provider) {
			@Override
			protected void populateItem(final Item<Comment> item) {
				final Comment comment = item.getModelObject();
				item.add(new Label("author", comment.getAuthor()));
				item.add(new SmartLinkMultiLineLabel("text", comment.getText()
						.getValue()).setVisible(setting.showText));
				item.add(new Label("date", ""+comment.getDate()));
				item.add(new ExternalLink("comments", CommentHelper
						.getUrlPath(comment)+"#disqus_thread"));
				item.add(new ExternalLink("viewpost", CommentHelper
						.getUrlPath(comment)).add(new Label("subject", comment
						.getSubject())));
			}
		};

		dataView.setItemsPerPage(5);
		provider.setSort("date", false);

		add(dataView);

		add(new SimplePagingNavigator("navigator", dataView));

		//add
		add(new Link<String>("add1") {
			@Override
			public void onClick() {
				setResponsePage(new AddPage(setting.id));
			}
		}.setVisible(setting.canPost || admin));
		add(new Link<String>("add2") {
			@Override
			public void onClick() {
				setResponsePage(new AddPage(setting.id));
			}
		}.setVisible(setting.canPost || admin));
		//disqus/comments
		add(new DisqusCountPanel("disqus"));

		addSidebarPanel(new TocPanel(nextSidebarId(), provider));
	}

}
