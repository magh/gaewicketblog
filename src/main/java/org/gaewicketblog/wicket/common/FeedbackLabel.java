package org.gaewicketblog.wicket.common;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

@SuppressWarnings("serial")
public class FeedbackLabel extends FeedbackPanel {

	public FeedbackLabel(String id, Component component) {
		super(id, new ComponentFeedbackMessageFilter(component));
	}

}
