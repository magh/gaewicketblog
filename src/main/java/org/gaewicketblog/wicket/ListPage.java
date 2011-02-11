package org.gaewicketblog.wicket;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkMultiLineLabel;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.gaewicketblog.common.AppEngineHelper;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.CommentHelper;
import org.gaewicketblog.wicket.common.DisqusCountPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ListPage extends BorderPage {

	private final static Logger log = LoggerFactory.getLogger(ListPage.class);

	public ListPage() {
		super();
		int id = Constants.NEWS;
		String path = RequestCycle.get().getRequest().getPath();
		log.debug("path="+path);
		if(path.endsWith(Constants.NEWS_STR)){
			id = Constants.NEWS;
		}else if(path.endsWith(Constants.FAQ_STR)){
			id = Constants.FAQ;
		}else if(path.endsWith(Constants.HELP_STR)){
			id = Constants.HELP;
		}else if(path.endsWith(Constants.ISSUES_STR)){
			id = Constants.ISSUES;
		}else if(path.endsWith(Constants.ABOUT_STR)){
			id = Constants.ABOUT;
		}
		CommentProvider provider = new CommentProvider(id);
		init(id, provider);
	}

	public ListPage(long id) {
		super();
		CommentProvider provider = new CommentProvider(id);
		init(id, provider);
	}
	
	/**
	 * To display arbitrary comments, e.g. from search.
	 * @param comments
	 */
	public ListPage(List<Comment> comments){
		super();
		FixedCommentProvider provider = new FixedCommentProvider(comments);
		init(Constants.SEARCH, provider);
	}
	
	private void init(final long id, SortableDataProvider<Comment> provider) {
		log.debug("ListPage<long> "+id);
		final ListPageSettings settings;
		switch((int)id){
		case Constants.SEARCH:
			settings = new ListPageSettings(false, true,
					getString("listpage.search.title"),
					getString("listpage.search.results"));
			break;
		case Constants.FAQ:
			settings = new ListPageSettings(false, true,
					getString("borderpage.li.faq.title"),
					getString("borderpage.li.faq.description"));
			break;
		case Constants.HELP:
			settings = new ListPageSettings(false, true,
					getString("borderpage.li.help.title"),
					getString("borderpage.li.help.description"));
			break;
		case Constants.ISSUES:
			settings = new ListPageSettings(true, false,
					getString("borderpage.li.issues.title"),
					getString("borderpage.li.issues.description"));
			break;
		case Constants.ABOUT:
			settings = new ListPageSettings(false, true,
					getString("borderpage.li.about.title"),
					getString("borderpage.li.about.description"));
			break;
//		case Constants.NEWS:
		default:
			settings = new ListPageSettings(false, true,
					getString("borderpage.li.news.title"),
					getString("borderpage.li.news.description"));
		}

		add(new Label("topic", settings.topic));
		add(new Label("topicdesc", settings.topicdesc));

		String adminemail = getString("admin.email");
		boolean admin = AppEngineHelper.isAdmin(adminemail);

		final DataView<Comment> dataView = new DataView<Comment>("sorting", provider) {
			@Override
			protected void populateItem(final Item<Comment> item) {
				final Comment comment = item.getModelObject();
				item.add(new Label("author", comment.getAuthor()));
				item.add(new SmartLinkMultiLineLabel("text", comment.getText()
						.getValue()).setVisible(settings.showText));
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

		add(new PagingNavigator("navigator", dataView));

		//add
		add(new Link<String>("add1") {
			@Override
			public void onClick() {
				setResponsePage(new AddPage(id, ""));
			}
		}.setVisible(settings.canPost || admin));
		add(new Link<String>("add2") {
			@Override
			public void onClick() {
				setResponsePage(new AddPage(id, ""));
			}
		}.setVisible(settings.canPost || admin));
		//disqus/comments
		add(new DisqusCountPanel("disqus"));

		addSidebarPanel(new TocPanel(nextSidebarId(), provider));
	}

	private static class ListPageSettings implements Serializable {
		private boolean canPost;
		private boolean showText;
		private String topic;
		private String topicdesc;
		public ListPageSettings(boolean canPost, boolean showText,
				String topic, String topicdesc) {
			this.canPost = canPost;
			this.showText = showText;
			this.topic = topic;
			this.topicdesc = topicdesc;
		}
	}

}
