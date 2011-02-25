package org.gaewicketblog.wicket.page;

import org.apache.wicket.Component;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class LoginPage extends BorderPage {
	
	public final static String MOUNTPATH = "/login";

	private final static Logger log = LoggerFactory.getLogger(ListPage.class);

	public LoginPage() {
		redirectToLogin(this, "/");
	}

	public static void redirectToLogin(Component context, String url) {
		if (url == null) {
			url = "/";
		}
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		String redirectUrl;
		if (user != null) {
			log.debug("user=" + user.toString());
			redirectUrl = userService.createLogoutURL(url);
		} else {
			redirectUrl = userService.createLoginURL(url);
		}
		context.getRequestCycle().setRequestTarget(
				new RedirectRequestTarget(redirectUrl));
	}

}
