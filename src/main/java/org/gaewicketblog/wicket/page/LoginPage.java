package org.gaewicketblog.wicket.page;

import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class LoginPage extends BorderPage {

	private final static Logger log = LoggerFactory.getLogger(ListPage.class);

	public LoginPage(){
		UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        String redirectUrl;
        if (user != null) {
        	log.debug("user="+user.toString());
        	redirectUrl = "/";
        } else {
        	log.warn("user is null");
        	redirectUrl = userService.createLoginURL("/");
        }
    	getRequestCycle().setRequestTarget(new RedirectRequestTarget(redirectUrl));
	}

}
