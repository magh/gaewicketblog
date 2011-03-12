package org.gaewicketblog.wicket.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.CommentHelper;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class FixedCommentProvider extends SortableDataProvider<Comment>
		implements ICommentProvider {

//	private final static Logger log = LoggerFactory.getLogger(CommentProvider.class);

	private List<Comment> comments;

	public FixedCommentProvider(List<Comment> comments) {
		this.comments = comments;
	}

	public Iterator<Comment> iterator(int first, int count) {
		SortParam sp = getSort();
		String property = sp.getProperty();
		Comparator<Comment> cmp;
		if(SORT_DATE.equals(property)) {
			cmp = CommentHelper.byDate;
		}else if(SORT_TEXT.equals(property)) {
			cmp = CommentHelper.byText;
		}else if(SORT_ID.equals(property)) {
			cmp = CommentHelper.byId;
		}else if(SORT_AUTHOR.equals(property)) {
			cmp = CommentHelper.byAuthor;
		}else if(SORT_SUBJECT.equals(property)) {
			cmp = CommentHelper.bySubject;
		}else if(SORT_COMMENTS.equals(property)) {
			cmp = CommentHelper.byComments;
		}else if(SORT_STATUS.equals(property)) {
			cmp = CommentHelper.byStatus;
		}else if(SORT_TYPE.equals(property)) {
			cmp = CommentHelper.byType;
		}else if(SORT_VOTES.equals(property)) {
			cmp = CommentHelper.byVotes;
		}else if(SORT_STARRED.equals(property)) {
			UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();
			cmp = CommentHelper.byStarred(user != null ? user.getUserId()
					: null);
		}else{
			cmp = CommentHelper.byDate;
		}
		if(sp.isAscending()){
			cmp = Collections.reverseOrder(cmp);
		}
		Collections.sort(comments, cmp);
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
