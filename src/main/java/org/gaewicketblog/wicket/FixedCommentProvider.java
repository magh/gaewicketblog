package org.gaewicketblog.wicket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.CommentHelper;

@SuppressWarnings("serial")
public class FixedCommentProvider extends SortableDataProvider<Comment> {

//	private final static Logger log = LoggerFactory.getLogger(CommentProvider.class);

	private List<Comment> comments;

	public FixedCommentProvider(List<Comment> comments) {
		this.comments = comments;
	}

	public Iterator<Comment> iterator(int first, int count) {
		SortParam sp = getSort();
		String property = sp.getProperty();
		if("date".equals(property)) {
			Collections.sort(comments, sp.isAscending() ? CommentHelper.byDate
					: Collections.reverseOrder(CommentHelper.byDate));
		}else if("text".equals(property)) {
			Collections.sort(comments, sp.isAscending() ? CommentHelper.byText
					: Collections.reverseOrder(CommentHelper.byText));
		}else if("id".equals(property)) {
			Collections.sort(comments, sp.isAscending() ? CommentHelper.byId
					: Collections.reverseOrder(CommentHelper.byId));
		}else{
			Collections.sort(comments, sp.isAscending() ? CommentHelper.byDate
					: Collections.reverseOrder(CommentHelper.byDate));
		}
		List<Comment> temp = new ArrayList<Comment>((List<Comment>) comments
				.subList(first, first + count));
		return temp.iterator();
	}

	public int size() {
		return comments.size();
	}

	public IModel<Comment> model(Comment object) {
		return new Model<Comment>(object);
	}

}
