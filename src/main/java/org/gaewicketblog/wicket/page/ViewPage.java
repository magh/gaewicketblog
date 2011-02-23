package org.gaewicketblog.wicket.page;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.gaewicketblog.common.AppEngineHelper;
import org.gaewicketblog.common.DbHelper;
import org.gaewicketblog.common.Util;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.TopicSetting;
import org.gaewicketblog.model.TopicSettingHelper;
import org.gaewicketblog.wicket.application.BlogApplication;
import org.gaewicketblog.wicket.common.DisqusPanel;
import org.gaewicketblog.wicket.common.ImgSmartLinkMultiLineLabel;
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
		add(new ImgSmartLinkMultiLineLabel("text", comment.getText().getValue()));
		String note = comment.getNote() != null ? comment.getNote().getValue() : "";
		add(new ImgSmartLinkMultiLineLabel("note", note).setVisible(!Util
				.isEmpty(note)));
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
				//TODO confirm
				DbHelper.delete(comment);
				BlogApplication app = (BlogApplication) getApplication();
				TopicSetting parent = TopicSettingHelper.getById(app.topics,
						comment.getParentid());
				String parentpath;
				if(parent == null) {
					log.warn("no parent path for parentid: "+comment.getParentid());
					parentpath = "/";
				}else{
					parentpath = "/" + parent.path;
				}
				getRequestCycle().setRequestTarget(
						new RedirectRequestTarget(parentpath));
			}
		}.setVisible(admin));
		//disqus/comments
		String uri = ((WebRequest) getRequest()).getHttpServletRequest()
				.getRequestURL().toString();
		String shortname = getString("disqus.shortname");
		add(new DisqusPanel("disqus", shortname, "" + comment.getId(), uri, comment.getSubject()));
	}

}
