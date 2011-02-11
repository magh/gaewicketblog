package org.gaewicketblog.wicket;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.resources.StyleSheetReference;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.gaewicketblog.model.Comment;

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
		add(new StyleSheetReference("css", getClass(), "css/style.css"));

		add(new Image("logo", "images/icon.png"));

		RepeatingView headerMenu = new RepeatingView("headermenu");
		addHeaderMenuLink(headerMenu, "/" + Constants.NEWS_STR,
				getString("borderpage.li.news.title"),
				getString("borderpage.li.news.description"));
		addHeaderMenuLink(headerMenu, "/" + Constants.HELP_STR,
				getString("borderpage.li.help.title"),
				getString("borderpage.li.help.description"));
		addHeaderMenuLink(headerMenu, "/" + Constants.FAQ_STR,
				getString("borderpage.li.faq.title"),
				getString("borderpage.li.faq.description"));
		addHeaderMenuLink(headerMenu, "/" + Constants.ISSUES_STR,
				getString("borderpage.li.issues.title"),
				getString("borderpage.li.issues.description"));
		addHeaderMenuLink(headerMenu, "/" + Constants.ABOUT_STR,
				getString("borderpage.li.about.title"),
				getString("borderpage.li.about.description"));
		add(headerMenu);

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
