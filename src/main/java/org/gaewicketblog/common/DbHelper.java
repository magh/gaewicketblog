package org.gaewicketblog.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.gaewicketblog.model.Comment;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class DbHelper {

	public static int count(Query query) {
		query.setResult("count(this)");
		Object obj = query.execute();
		// log.error("size: "+obj);
		return (Integer)obj;
	}

	public static <T> T merge(T obj){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			return pm.makePersistent(obj);
		} finally {
			pm.close();
		}
	}

	public static <T> List<? extends T> mergeAll(List<? extends T> objs){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			return (List<? extends T>) pm.makePersistentAll(objs);
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

	public static Comment getComment(long id){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Key key = KeyFactory.createKey(Comment.class.getSimpleName(), id);
			return pm.getObjectById(Comment.class, key);
		} finally {
			pm.close();
		}
	}

	public static Comment getCommentByLink(String link){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(Comment.class);
		query.setFilter("link == linkParam");
		query.declareParameters("String linkParam");
		query.setUnique(true);
		try{
			return (Comment) query.execute(link);
		}finally{
			query.closeAll();
			pm.close();
		}
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

	public static List<Comment> getComments(long parentid) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(Comment.class);
		try {
			query.setFilter("parentid == parentidParam");
			query.declareParameters("Long parentidParam");
			return new ArrayList<Comment>((List<Comment>) query
					.execute(parentid));
		} finally {
			query.closeAll();
			pm.close();
		}
	}

	public static List<Comment> getAllComments(){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			Extent<Comment> extent = pm.getExtent(Comment.class);
			List<Comment> res = new ArrayList<Comment>();
			for (Comment comment : extent) {
				res.add(comment);
			}
			return res;
		}finally{
			pm.close();
		}
	}

	public static Map<Long, Comment> getAllCommentsAsMap(){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			Extent<Comment> extent = pm.getExtent(Comment.class);
			Map<Long, Comment> res = new HashMap<Long, Comment>();
			for (Comment comment : extent) {
				res.put(comment.getId(), comment);
			}
			return res;
		}finally{
			pm.close();
		}
	}

}
