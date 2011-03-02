package org.gaewicketblog.wicket.provider;

import java.util.ArrayList;
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
		query.setIgnoreCache(true);
		query.setRange(first, first+count);
		query.declareParameters("Long parentidParam");
		try{
//			return Collections.synchronizedList(query.execute(parentid))
//					.iterator();
			List<Comment> temp = new ArrayList<Comment>((List<Comment>) query
					.execute(parentid));
			return temp.iterator();
		}finally{
			query.closeAll();
			pm.close();
		}
	}

	public int size() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(Comment.class, "parentid == "+parentid);
		return DbHelper.count(query);
	}

	public IModel<Comment> model(Comment object) {
		return new Model<Comment>(object);
	}

}
