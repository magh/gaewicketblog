package org.gaewicketblog.wicket.page;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.resources.StyleSheetReference;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.TopicSetting;
import org.gaewicketblog.wicket.application.BlogApplication;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class BorderPage extends WebPage {

	private WebMarkupContainer maincontent;

	public BorderPage() {
		super();
		init();
	}

	public BorderPage(PageParameters parameters){
		super(parameters);
		init();
	}

	public BorderPage(IModel<Comment> commentModel) {
		super(commentModel);
		init();
	}
	
	private static void addHeaderMenuLink(RepeatingView headerMenu,
			String href, String title, String desc) {
		headerMenu.add(new WebMarkupContainer(headerMenu.newChildId())
				.add(new ExternalLink("menulink", href, title)
						.add(new AttributeModifier("title", true,
								new Model<String>(desc)))));
	}

	private void init(){
		add(new StyleSheetReference("css", BlogApplication.class, "css/style.css"));

		add(new Image("logo", new ResourceReference(BlogApplication.class,
				"images/icon.png")));

		RepeatingView headerMenu = new RepeatingView("headermenu");
		add(headerMenu);
		BlogApplication app = (BlogApplication) getApplication();
		for (TopicSetting topic : app.topics) {
			addHeaderMenuLink(headerMenu, "/" + topic.path, topic.topic,
					topic.topicdesc);
		}
		
		//user/login
		UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        final String url = getRequest().getURL();
		add(new Link<Void>("loginlink"){
			@Override
			public void onClick() {
				LoginPage.redirectToLogin(this, url);
			}
		}.add(new Label("logintext", user != null ? "logout " : "login")));

		maincontent = new WebMarkupContainer("maincontent",
				new Model<Boolean>()){
			@Override
			public boolean isTransparentResolver() {
				return true;
			}
		};
		add(maincontent);

		RepeatingView sidebar = new RepeatingView("sidebar");
		add(sidebar);
	}

	public String nextSidebarId(){
		RepeatingView sidebar = (RepeatingView) get("sidebar");
		return sidebar.newChildId();
	}

	public void addSidebarPanel(Component child){
		if(maincontent.getDefaultModelObject() == null){
			maincontent.setDefaultModelObject(true);
			// set max width to main content.
			maincontent.add(new AttributeModifier(
					"style", true, new Model<String>("width: 600px;")));
		}
		RepeatingView sidebar = (RepeatingView) get("sidebar");
		sidebar.add(child);
	}

}
