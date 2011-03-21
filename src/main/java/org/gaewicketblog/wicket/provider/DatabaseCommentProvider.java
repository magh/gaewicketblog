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
import org.gaewicketblog.common.PMF;
import org.gaewicketblog.common.Util;
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
	
	private int[] statuses;

	public DatabaseCommentProvider(long parentid) {
		this.parentid = parentid;
	}

	public DatabaseCommentProvider(long parentid, int[] statuses) {
		this.parentid = parentid;
		this.statuses = statuses;
	}

	public Iterator<Comment> iterator(int first, int count) {
		SortParam sp = getSort();
		if(sp == null){
			sp = new SortParam("", true);
		}
		String property = sp.getProperty();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(Comment.class);
		//order
		if(SORT_STARRED.equals(property)) {
			// post sort and range
		}else{
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
			query.setRange(first, first+count);
		}
		//FIXME below should be improved
		List<Comment> temp;
		try {
			// filter on statuses
			if(statuses != null){
				query.setFilter("parentid == "+parentid+" && :p1.contains(status)");
				temp = new ArrayList<Comment>((List<Comment>) query
						.execute(Util.asList(statuses)));
			}else{
				query.setFilter("parentid == parentidParam");
				query.declareParameters("Long parentidParam");
				temp = new ArrayList<Comment>((List<Comment>) query
						.execute(parentid));
			}
		} finally {
			query.closeAll();
			pm.close();
		}
		//FIXME inefficient
		//post sort and range
		if(SORT_STARRED.equals(property)){
			UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();
			Comparator<Comment> cmp = CommentHelper
					.byStarred(user != null ? user.getUserId() : null);
			Collections.sort(temp, cmp);
			int size = temp.size();
			if(first + count >= size){
				temp = temp.subList(first, size-1);
			}else{
				temp = temp.subList(first, first + count);
			}
		}
		return temp.iterator();
	}

	public int size() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(Comment.class);
		query.setResult("count(this)");
		try{
			if(statuses != null){
				query.setFilter("parentid == "+parentid+" && :p1.contains(status)");
				return (Integer)query.execute(Util.asList(statuses));
			}else{
				query.setFilter("parentid == parentidParam");
				query.declareParameters("Long parentidParam");
				return (Integer)query.execute(parentid);
			}
		}finally{
			query.closeAll();
			pm.close();
		}
	}

	public IModel<Comment> model(Comment object) {
		return new Model<Comment>(object);
	}

}
