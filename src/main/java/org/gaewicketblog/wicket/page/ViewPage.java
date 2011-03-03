package org.gaewicketblog.wicket.page;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.gaewicketblog.common.AppEngineHelper;
import org.gaewicketblog.common.DbHelper;
import org.gaewicketblog.common.Util;
import org.gaewicketblog.common.WicketHelper;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.CommentHelper;
import org.gaewicketblog.model.TopicSetting;
import org.gaewicketblog.model.TopicSettingHelper;
import org.gaewicketblog.wicket.application.BlogApplication;
import org.gaewicketblog.wicket.common.DisqusPanel;
import org.gaewicketblog.wicket.panel.ContactPanel;
import org.gaewicketblog.wicket.panel.RecentPostsPanel;
import org.gaewicketblog.wicket.panel.SearchPanel;
import org.gaewicketblog.wicket.provider.DatabaseCommentProvider;
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
		String path = WicketHelper.getCurrentRestfulPath();
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
        final Comment comment = commentModel.getObject();
		add(new Label("author", comment.getAuthor()));
		add(new Label("subject", comment.getSubject()));
		add(new MultiLineLabel("text", comment.getText().getValue())
				.setEscapeModelStrings(false));
		String note = comment.getNote() != null ? comment.getNote().getValue() : "";
		add(new MultiLineLabel("note", note).setVisible(!Util.isEmpty(note))
				.setEscapeModelStrings(false));
		add(new Label("date", ""+comment.getDate()));

		Integer status = comment.getStatus();
		boolean showStatus = status != null && status != Comment.STATUS_UNASSIGNED;
		add(CommentHelper.newStatusColorLabel(this, "status", status)
				.setVisible(showStatus));

		String adminemail = getString("admin.email");
		boolean canedit = AppEngineHelper.isCurrentUser(new String[] {
				adminemail, comment.getEmail() });

		//edit
		add(new Link<String>("edit"){
			@Override
			public void onClick() {
				setResponsePage(new EditPage("Update", comment));
			}
		}.setVisible(canedit));
		//delete
		add(new Link<String>("delete"){
			@Override
			public void onClick() {
				//TODO confirm dialog for delete
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
		}.setVisible(canedit));
		//disqus/comments
		String uri = ((WebRequest) getRequest()).getHttpServletRequest()
				.getRequestURL().toString();
		String shortname = getString("disqus.shortname");
		add(new DisqusPanel("disqus", shortname, "" + comment.getId(), uri, comment.getSubject()));

		addSidebarPanel(new SearchPanel(nextSidebarId()));
		addSidebarPanel(new ContactPanel(nextSidebarId()));
		DatabaseCommentProvider provider = new DatabaseCommentProvider(comment.getParentid());
		addSidebarPanel(new RecentPostsPanel(nextSidebarId(), provider));
	}

}
