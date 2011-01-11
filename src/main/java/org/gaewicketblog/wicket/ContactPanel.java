package org.gaewicketblog.wicket;

import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;

@SuppressWarnings("serial")
public class ContactPanel extends Panel {

	public ContactPanel(String id) {
		super(id);
		add(new Image("twitter", "images/twitter.png"));
		add(new Image("facebook", "images/facebook.png"));
		add(new Image("email", "images/email.png"));
	}

}
