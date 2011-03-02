package org.gaewicketblog.wicket.panel;

import java.util.List;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.gaewicketblog.common.DbHelper;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.CommentHelper;
import org.gaewicketblog.model.TopicSetting;
import org.gaewicketblog.wicket.page.ListPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class SearchPanel extends Panel {

	private final static Logger log = LoggerFactory.getLogger(SearchPanel.class);

	public SearchPanel(String id) {
		super(id);

		final Model<String> model = new Model<String>();
		Form<String> searchform = new Form<String>("searchform"){
			@Override
			protected void onSubmit() {
				String in = model.getObject();
				log.info("search: "+in);
				if(in == null || in.length() <= 3) {
					error("Invalid search term: "+in);
				} else {
					in = in.toLowerCase();
					List<Comment> comments = DbHelper.getAllComments();
					TopicSetting search = new TopicSetting(-1, "/", false,
							true, "Search", "Search", null);
					setResponsePage(new ListPage(search, CommentHelper.search(
							comments, in, null)));
				}
			}
		};
		searchform.add(new TextField<String>("search", model));
		add(searchform);
	}

}
