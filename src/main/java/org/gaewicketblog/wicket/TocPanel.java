package org.gaewicketblog.wicket;

import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.CommentHelper;

@SuppressWarnings("serial")
public class TocPanel extends Panel {
	
	private final static int MAX = 30;

	public TocPanel(String id, SortableDataProvider<Comment> provider) {
		super(id);
		RepeatingView posts = new RepeatingView("posts");
		add(posts);
		
		int size = provider.size();
		Iterator<? extends Comment> iterator = provider.iterator(0, size > MAX ? MAX : size);
		for (Iterator<? extends Comment> it = iterator; it.hasNext();) {
			Comment comment = it.next();
			posts.add(new WebMarkupContainer(posts.newChildId())
					.add(new ExternalLink("postlink",
							CommentHelper.getUrlPath(comment), comment
									.getSubject())));
		}
	}

}
