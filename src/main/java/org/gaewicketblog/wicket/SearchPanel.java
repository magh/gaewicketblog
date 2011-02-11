package org.gaewicketblog.wicket;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import org.gaewicketblog.common.PMF;
import org.gaewicketblog.model.Comment;
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
					List<Comment> found = new ArrayList<Comment>();
					PersistenceManager pm = PMF.get().getPersistenceManager();
					Extent<Comment> extent = pm.getExtent(Comment.class);
					for (Comment comment : extent) {
						if (comment.getAuthor().contains(in)
								|| comment.getSubject().contains(in)
								|| comment.getText().getValue().contains(in)) {
							found.add(comment);
						}
					}
					setResponsePage(new ListPage(found));
				}
			}
		};
		searchform.add(new TextField<String>("search", model));
		add(searchform);
	}

}
