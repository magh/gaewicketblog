package org.gaewicketblog.wicket.panel;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.gaewicketblog.common.Util;
import org.gaewicketblog.wicket.application.BlogApplication;
import org.gaewicketblog.wicket.common.FacebookLikePanel;

@SuppressWarnings("serial")
public class ContactPanel extends Panel {

	public ContactPanel(String id) {
		super(id);

		String emailimagehref = getString("contactpanel.email.image.href");
		add(new ExternalLink("emaillink", emailimagehref).add(new Image(
				"emailimg", new ResourceReference(BlogApplication.class,
						"images/email.png"))).setVisible(!Util.isEmpty(emailimagehref)));

		String twitterimagehref = getString("contactpanel.twitter.image.href");
		add(new ExternalLink("twitterlink", twitterimagehref).add(new Image(
				"twitterimg", new ResourceReference(BlogApplication.class,
						"images/twitter.png"))).setVisible(!Util.isEmpty(twitterimagehref)));

		String facebookimagehref = getString("contactpanel.facebook.image.href");
		add(new ExternalLink("facebooklink", facebookimagehref).add(new Image(
				"facebookimg", new ResourceReference(BlogApplication.class,
						"images/facebook.png"))).setVisible(!Util.isEmpty(facebookimagehref)));

		String facebooklikehref = getString("contactpanel.facebook.like.href");
		add(new FacebookLikePanel("facebooklike", facebooklikehref).setVisible(!Util
				.isEmpty(facebooklikehref)));
		
		setVisible(!Util.isEmpty(emailimagehref)
				|| !Util.isEmpty(twitterimagehref)
				|| !Util.isEmpty(facebookimagehref)
				|| !Util.isEmpty(facebooklikehref));
	}

}
