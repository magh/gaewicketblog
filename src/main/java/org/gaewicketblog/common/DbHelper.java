package org.gaewicketblog.common;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.gaewicketblog.model.Comment;

public class DbHelper {

	public static int count(Query query){
		query.setResult("count(this)");
		try{
			Object obj = query.execute();
			// log.error("size: "+obj);
			return (Integer)obj;
		}finally{
			query.closeAll();
		}
	}

	public static Object merge(Object obj){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			return pm.makePersistent(obj);
		} finally {
			pm.close();
		}
	}

	public static void delete(Object obj){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.deletePersistent(obj);
		} finally {
			pm.close();
		}
	}

	public static void delete(Comment comment) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			comment = pm.makePersistent(comment);
			delete(comment, pm);
		} finally {
			pm.close();
		}
	}

	public static void delete(Comment comment, PersistenceManager pm) {
		List<Comment> comments = getComments(comment.getId(), pm);
		if(comments != null) {
			for (Comment child : comments) {
				delete(child, pm);
			}
		}
		pm.deletePersistent(comment);
	}

	public static List<Comment> getComments(long parentid, PersistenceManager pm){
		Query query = pm.newQuery(Comment.class);
		query.setFilter("parentid == parentidParam");
		query.declareParameters("Long parentidParam");
		try{
			return new ArrayList<Comment>((List<Comment>) query
					.execute(parentid));
		}finally{
			query.closeAll();
		}
	}

}
