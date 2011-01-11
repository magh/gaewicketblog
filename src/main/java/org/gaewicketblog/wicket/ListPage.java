package org.gaewicketblog.wicket;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkMultiLineLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.gaewicketblog.common.AppEngineHelper;
import org.gaewicketblog.common.DbHelper;
import org.gaewicketblog.common.PMF;
import org.gaewicketblog.model.Comment;

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
		}else if(path.endsWith(Constants.BUGS_STR)){
			id = Constants.BUGS;
		}else if(path.endsWith(Constants.FAQ_STR)){
			id = Constants.FAQ;
		}else if(path.endsWith(Constants.HELP_STR)){
			id = Constants.HELP;
		}else if(path.endsWith(Constants.FEATURE_STR)){
			id = Constants.FEATURE;
		}else if(path.endsWith(Constants.ABOUT_STR)){
			id = Constants.ABOUT;
		}
		init(id);
	}

	public ListPage(long id) {
		super();
		init(id);
	}
	
	/**
	 * To display arbitrary comments, e.g. from search.
	 * @param comments
	 */
	public ListPage(List<Comment> comments){
		super();
		add(new Label("topic", getString("listpage.search.title")));
		add(new Label("topicdesc", getString("listpage.search.results")));

		FixedCommentProvider provider = new FixedCommentProvider(comments);
		final DataView<Comment> dataView = new DataView<Comment>("sorting", provider) {
			@Override
			protected void populateItem(final Item<Comment> item) {
				final Comment comment = item.getModelObject();
				item.add(new Label("author", comment.getAuthor()));
				item.add(new SmartLinkMultiLineLabel("text", comment.getText().getValue()));
				item.add(new Label("date", ""+comment.getDate()));
				item.add(new Label("comments", "-"));
				item.add(new Link<String>("viewpost") {
					@Override
					public void onClick() {
						setResponsePage(new ViewPage(item.getModel()));
					}
				}.add(new Label("subject", comment.getSubject())));
				item.add(new Link<String>("reply") {
					@Override
					public void onClick() {}
				}.setVisible(false));
			}
		};

		dataView.setItemsPerPage(5);
		provider.setSort("date", false);

		add(dataView);

		add(new PagingNavigator("navigator", dataView));
		
		//add (disabled)
		add(new Link<String>("add1") {
			@Override
			public void onClick() {}
		}.setVisible(false));
		add(new Link<String>("add2") {
			@Override
			public void onClick() {}
		}.setVisible(false));
	}

	private void init(final long id) {
		log.debug("ListPage<long> "+id);
		boolean canPost;
		switch((int)id){
		case Constants.BUGS:
			add(new Label("topic", getString("borderpage.li.bugs.title")));
			add(new Label("topicdesc", getString("borderpage.li.bugs.description")));
			canPost = true;
			break;
		case Constants.FAQ:
			add(new Label("topic", getString("borderpage.li.faq.title")));
			add(new Label("topicdesc", getString("borderpage.li.faq.description")));
			canPost = false;
			break;
		case Constants.HELP:
			add(new Label("topic", getString("borderpage.li.help.title")));
			add(new Label("topicdesc", getString("borderpage.li.help.description")));
			canPost = false;
			break;
		case Constants.FEATURE:
			add(new Label("topic", getString("borderpage.li.features.title")));
			add(new Label("topicdesc", getString("borderpage.li.features.description")));
			canPost = true;
			break;
		case Constants.ABOUT:
			add(new Label("topic", getString("borderpage.li.about.title")));
			add(new Label("topicdesc", getString("borderpage.li.about.description")));
			canPost = false;
			break;
//			case Constants.NEWS:
//			add(new Label("topic", getString("borderpage.li.news.title")));
//			add(new Label("topicdesc", getString("borderpage.li.news.description")));
//			canPost = false;
//			break;
		default:
			add(new Label("topic", getString("borderpage.li.news.title")));
			add(new Label("topicdesc", getString("borderpage.li.news.description")));
			canPost = false;
		}

		CommentProvider provider = new CommentProvider(id);
		final DataView<Comment> dataView = new DataView<Comment>("sorting", provider) {
			@Override
			protected void populateItem(final Item<Comment> item) {
				final Comment comment = item.getModelObject();
				item.add(new Label("author", comment.getAuthor()));
				item.add(new SmartLinkMultiLineLabel("text", comment.getText().getValue()));
				item.add(new Label("date", ""+comment.getDate()));
				PersistenceManager pm = PMF.get().getPersistenceManager();
				Query query = pm.newQuery(Comment.class, "parentid == "+comment.getId());
				try{
					int comments = DbHelper.count(query);
					item.add(new Label("comments", ""+comments));
				}finally{
					pm.close();
				}
				item.add(new Link<String>("viewpost") {
					@Override
					public void onClick() {
						setResponsePage(new ViewPage(item.getModel()));
					}
				}.add(new Label("subject", comment.getSubject())));
				item.add(new Link<String>("reply") {
					@Override
					public void onClick() {
						String re = getString("listpage.reply.prepend");
						setResponsePage(new AddPage(comment.getId(), re
								+ comment.getSubject()));
					}
				});
			}
		};

		dataView.setItemsPerPage(5);
		provider.setSort("date", false);

		add(dataView);

		add(new PagingNavigator("navigator", dataView));

		boolean admin = AppEngineHelper.isAdmin();

		//add
		add(new Link<String>("add1") {
			@Override
			public void onClick() {
				setResponsePage(new AddPage(id, ""));
			}
		}.setVisible(canPost || admin));
		add(new Link<String>("add2") {
			@Override
			public void onClick() {
				setResponsePage(new AddPage(id, ""));
			}
		}.setVisible(canPost || admin));
	}

}
