package org.gaewicketblog.wicket.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.gaewicketblog.common.DbHelper;
import org.gaewicketblog.common.PMF;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.CommentHelper;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class DatabaseCommentProvider extends SortableDataProvider<Comment>
		implements ICommentProvider {

//	private final static Logger log = LoggerFactory.getLogger(CommentProvider.class);

	private long parentid;

	public DatabaseCommentProvider(long parentid) {
		this.parentid = parentid;
	}

	public Iterator<Comment> iterator(int first, int count) {
		SortParam sp = getSort();
		if(sp == null){
			sp = new SortParam("", true);
		}
		String property = sp.getProperty();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(Comment.class);
		query.setFilter("parentid == parentidParam");
		//order
		if(SORT_DATE.equals(property)) {
			query.setOrdering(sp.isAscending() ? "date" : "date desc");
		}else if(SORT_TEXT.equals(property)) {
			query.setOrdering(sp.isAscending() ? "text" : "text desc");
		}else if(SORT_ID.equals(property)) {
			query.setOrdering(sp.isAscending() ? "id" : "id desc");
		}else if(SORT_AUTHOR.equals(property)) {
			query.setOrdering(sp.isAscending() ? "author" : "author desc");
		}else if(SORT_SUBJECT.equals(property)) {
			query.setOrdering(sp.isAscending() ? "subject" : "subject desc");
		}else if(SORT_STATUS.equals(property)) {
			query.setOrdering(sp.isAscending() ? "status" : "status desc");
		}else if(SORT_VOTES.equals(property)) {
			query.setOrdering(sp.isAscending() ? "votes" : "votes desc");
		}else if(SORT_COMMENTS.equals(property)) {
			query.setOrdering(sp.isAscending() ? "comments" : "comments desc");
		}else{
			query.setOrdering(sp.isAscending() ? "date" : "date desc");
		}
//		query.setIgnoreCache(true);
		query.declareParameters("Long parentidParam");
		// handle special cases
		if(SORT_STARRED.equals(property)){
			//FIXME inefficient
			try {
				UserService userService = UserServiceFactory.getUserService();
				User user = userService.getCurrentUser();
				Comparator<Comment> cmp = CommentHelper
						.byStarred(user != null ? user.getUserId() : null);
				List<Comment> temp = new ArrayList<Comment>(
						(List<Comment>) query.execute(parentid));
				Collections.sort(temp, cmp);
				temp.subList(first, first + count);
				return temp.iterator();
			} finally {
				query.closeAll();
				pm.close();
			}
		}else{
			query.setRange(first, first+count);
			try{
//				return Collections.synchronizedList(query.execute(parentid))
//						.iterator();
				List<Comment> temp = new ArrayList<Comment>((List<Comment>) query
						.execute(parentid));
				return temp.iterator();
			}finally{
				query.closeAll();
				pm.close();
			}
		}
	}

	public int size() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(Comment.class, "parentid == "+parentid);
		try{
			return DbHelper.count(query);
		}finally{
			query.closeAll();
			pm.close();
		}
	}

	public IModel<Comment> model(Comment object) {
		return new Model<Comment>(object);
	}

}
