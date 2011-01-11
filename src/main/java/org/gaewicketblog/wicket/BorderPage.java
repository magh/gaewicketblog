package org.gaewicketblog.wicket;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.resources.StyleSheetReference;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;

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

	private void init(){
		add(new StyleSheetReference("css", getClass(), "css/style.css"));
		
		add(new Image("logo", "images/icon.png"));
		
		RepeatingView sidebar = new RepeatingView("sidebar");
		sidebar.add(new SearchPanel("1"));
		sidebar.add(new ContactPanel("2"));
		add(sidebar);
	}

}
