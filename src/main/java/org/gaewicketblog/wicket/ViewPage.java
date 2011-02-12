package org.gaewicketblog.wicket;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkMultiLineLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebRequest;
import org.gaewicketblog.common.AppEngineHelper;
import org.gaewicketblog.common.DbHelper;
import org.gaewicketblog.common.Util;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.wicket.common.DisqusPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.Text;

@SuppressWarnings("serial")
public class ViewPage extends BorderPage {

	private final static Logger log = LoggerFactory.getLogger(ListPage.class);

	public ViewPage(IModel<Comment> commentModel) {
		init(commentModel);
	}
	
	public ViewPage(long parentid){
		Comment comment = DbHelper.getComment(parentid);
		if(comment == null){
			log.error(getString("viewpage.error.commentnotfound")+" parentid="+parentid);
			String error = getString("viewpage.error.error");
			comment = new Comment(-1, error, new Text(error), error, error,
					error);
		}
		init(new Model<Comment>(comment));
	}

	public ViewPage() {
		String path = RequestCycle.get().getRequest().getPath();
		log.debug("<init> path="+path);
		long id = Util.parseLong(path, -1);
		Comment comment;
		if(id == -1){
			comment = DbHelper.getCommentByLink("/"+path);
		}else{
			comment = DbHelper.getComment(id);
		}
		if(comment == null){
			log.error(getString("viewpage.error.commentnotfound")+" id="+id);
			String error = getString("viewpage.error.error");
			comment = new Comment(-1, error, new Text(error), error, error,
					error);
		}
		init(new Model<Comment>(comment));
	}

	public void init(IModel<Comment> commentModel) {
		String adminemail = getString("admin.email");
		final boolean admin = AppEngineHelper.isAdmin(adminemail);
        final Comment comment = commentModel.getObject();
		add(new Label("author", comment.getAuthor()));
		add(new Label("subject", comment.getSubject()));
		add(new SmartLinkMultiLineLabel("text", comment.getText().getValue()));
		add(new Label("date", ""+comment.getDate()));

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
				setResponsePage(new ListPage(comment.getParentid()));
			}
		}.setVisible(admin));
		//disqus/comments
		String uri = ((WebRequest) getRequest()).getHttpServletRequest()
				.getRequestURL().toString();
		add(new DisqusPanel("disqus", "" + comment.getId(), uri, comment.getSubject()));
	}

}
