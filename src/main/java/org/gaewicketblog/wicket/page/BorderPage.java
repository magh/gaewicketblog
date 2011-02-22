package org.gaewicketblog.wicket.page;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.resources.StyleSheetReference;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.TopicSetting;
import org.gaewicketblog.wicket.application.BlogApplication;
import org.gaewicketblog.wicket.panel.ContactPanel;
import org.gaewicketblog.wicket.panel.SearchPanel;

public class BorderPage extends WebPage {

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

		RepeatingView sidebar = new RepeatingView("sidebar");
		add(sidebar);
		addSidebarPanel(new SearchPanel(sidebar.newChildId()));
		addSidebarPanel(new ContactPanel(sidebar.newChildId()));
	}

	public String nextSidebarId(){
		RepeatingView sidebar = (RepeatingView) get("sidebar");
		return sidebar.newChildId();
	}

	public void addSidebarPanel(Component child){
		RepeatingView sidebar = (RepeatingView) get("sidebar");
		sidebar.add(child);
	}

}
