package org.gaewicketblog.wicket.panel;

import org.apache.wicket.markup.html.panel.Panel;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.CommentHelper;

@SuppressWarnings("serial")
public class StatusDescriptionPanel extends Panel {

	public StatusDescriptionPanel(String id) {
		super(id);
		addStatusLabel(Comment.STATUS_UNASSIGNED);
		addStatusLabel(Comment.STATUS_OPEN_UNDERREVIEW);
		addStatusLabel(Comment.STATUS_OPEN_NEEDSINFO);
		addStatusLabel(Comment.STATUS_OPEN_STARTED);
		addStatusLabel(Comment.STATUS_CLOSED_COMPLETED);
		addStatusLabel(Comment.STATUS_CLOSED_DECLINED);
		addStatusLabel(Comment.STATUS_CLOSED_PENDING);
	}

	private void addStatusLabel(int status){
		add(CommentHelper.newStatusColorLabel(this, "status" + status, status));
	}

}
