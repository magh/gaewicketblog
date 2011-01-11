package org.gaewicketblog.wicket;

import javax.jdo.PersistenceManager;

import org.apache.wicket.extensions.markup.html.basic.SmartLinkMultiLineLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.gaewicketblog.common.AppEngineHelper;
import org.gaewicketblog.common.DbHelper;
import org.gaewicketblog.common.PMF;
import org.gaewicketblog.model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

@SuppressWarnings("serial")
public class ViewPage extends BorderPage {

	private final static Logger log = LoggerFactory.getLogger(ListPage.class);

	public ViewPage(long parentid){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Key key = KeyFactory.createKey(Comment.class.getSimpleName(), parentid);
			Comment comment = pm.getObjectById(Comment.class, key);
			if(comment == null){
				log.error(getString("viewpage.error.parentcommentnotfound")+" parentid="+parentid);
				String error = getString("viewpage.error.error");
				comment = new Comment(Constants.NEWS, error, new Text(error),
						error, error);
			}
			init(new Model<Comment>(comment));
		} finally {
			pm.close();
		}
	}

	public ViewPage(IModel<Comment> commentModel) {
		init(commentModel);
	}

	public void init(IModel<Comment> commentModel) {
		final boolean admin = AppEngineHelper.isAdmin();
        final Comment comment = commentModel.getObject();
		add(new Label("author", comment.getAuthor()));
		add(new Label("subject", comment.getSubject()));
		add(new SmartLinkMultiLineLabel("text", comment.getText().getValue()));
		add(new Label("date", ""+comment.getDate()));
		CommentProvider provider = new CommentProvider(comment.getId());
		add(new Label("comments", ""+provider.size()));
		
		final DataView<Comment> dataView = new DataView<Comment>("sorting", provider) {
			@Override
			protected void populateItem(final Item<Comment> item) {
				final Comment comment = item.getModelObject();
				item.add(new Label("author", comment.getAuthor()));
				item.add(new Label("subject", comment.getSubject()));
				item.add(new SmartLinkMultiLineLabel("text", comment.getText()
						.getValue()));
				item.add(new Label("date", ""+comment.getDate()));
				item.add(new Link<String>("editcomment"){
					@Override
					public void onClick() {
						setResponsePage(new UpdatePage(comment));
					}
				}.setVisible(admin));
				item.add(new Link<String>("deletecomment"){
					@Override
					public void onClick() {
						DbHelper.delete(comment);
					}
				}.setVisible(admin));
			}
		};

		dataView.setItemsPerPage(10);
		provider.setSort("date", true);

		add(dataView);

		add(new PagingNavigator("navigator", dataView));
		
		//add
		add(new Link<String>("add"){
			@Override
			public void onClick() {
				String re = getString("viewpage.reply.prepend");
				setResponsePage(new AddPage(comment.getId(), re
						+ comment.getSubject()));
			}
		});
		//back
		add(new Link<String>("back"){
			@Override
			public void onClick() {
				setResponsePage(new ListPage(comment.getParentid()));
			}
		});
		//edit
		add(new Link<String>("edit"){
			@Override
			public void onClick() {
				setResponsePage(new UpdatePage(comment));
			}
		}.setVisible(admin));
		//delete
		add(new Link<String>("delete"){
			@Override
			public void onClick() {
				DbHelper.delete(comment);
			}
		}.setVisible(admin));
	}

}
