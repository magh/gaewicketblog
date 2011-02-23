package org.gaewicketblog.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.gaewicketblog.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommentHelper {
	
	private final static Logger log = LoggerFactory.getLogger(CommentHelper.class);

//	private final static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd ");

	public static Comparator<Comment> byDate = new Comparator<Comment>() {
		@Override
		public int compare(Comment arg0, Comment arg1) {
			if (arg0 != null && arg1 != null) {
				return arg0.getDate().compareTo(arg1.getDate());
			}
			return -1;
		}
	};

	public static Comparator<Comment> byId = new Comparator<Comment>() {
		@Override
		public int compare(Comment arg0, Comment arg1) {
			if (arg0 != null && arg1 != null) {
				return Long.valueOf(arg0.getId()).compareTo(arg1.getId());
			}
			return -1;
		}
	};

	public static Comparator<Comment> byText = new Comparator<Comment>() {
		@Override
		public int compare(Comment arg0, Comment arg1) {
			try{
				return arg0.getText().getValue()
						.compareToIgnoreCase(arg1.getText().getValue());
			}catch(Exception e){
				log.error(e.getMessage(), e);
			}
			return -1;
		}
	};

	public static Comparator<Comment> bySubject = new Comparator<Comment>() {
		@Override
		public int compare(Comment arg0, Comment arg1) {
			try{
				return arg0.getSubject().compareToIgnoreCase(arg1.getSubject());
			}catch(Exception e){
				log.error(e.getMessage(), e);
			}
			return -1;
		}
	};

	public static Comparator<Comment> byAuthor = new Comparator<Comment>() {
		@Override
		public int compare(Comment arg0, Comment arg1) {
			try{
				return arg0.getAuthor().compareToIgnoreCase(arg1.getAuthor());
			}catch(Exception e){
				log.error(e.getMessage(), e);
			}
			return -1;
		}
	};

	public static Comment getById(List<Comment> tasks, long id) {
		for (Comment item : tasks) {
			if (item.getId() == id) {
				return item;
			}
		}
		return null;
	}

	public static List<Comment> getByText(List<Comment> items, String text) {
		List<Comment> res = new ArrayList<Comment>();
		for (Comment item : items) {
			if(item.getText().getValue().contains(text)){
				res.add(item);
			}
		}
		return res;
	}

	public static List<Comment> getByTextIgnoreCase(List<Comment> items, String text) {
		text = text.toUpperCase();
		List<Comment> res = new ArrayList<Comment>();
		for (Comment item : items) {
			if (item.getText().getValue().toUpperCase().contains(text)) {
				res.add(item);
			}
		}
		return res;
	}

	public static String getUrlPath(Comment comment) {
		if(!Util.isEmpty(comment.getLink())){
			return comment.getLink();
		}
		return "/"+comment.getId();
	}

	public static String genUrlPath(String subject) {
		return "/"+escape(subject.toLowerCase().replace(' ', '_'));
	}

	public static String escape(String in) {
		return in.replaceAll("[\\,/,?,:,\",*,<,>,|,\\',\\’,\\‘]", "");
	}

	public static String getStatusAsString(Integer status){
		switch(status){
		case Comment.STATUS_OPEN_NEEDSINFO:
			return "NI";
		case Comment.STATUS_OPEN_UNDERREVIEW:
			return "UR";
		case Comment.STATUS_OPEN_PLANNED:
			return "P";
		case Comment.STATUS_OPEN_STARTED:
			return "S";
		case Comment.STATUS_CLOSED_COMPLETED:
			return "C";
		case Comment.STATUS_CLOSED_DECLINED:
			return "D";
		case Comment.STATUS_CLOSED_DUPLICATE:
			return "DU";
		case Comment.STATUS_NOSTATUS:
			return "W";
		default:
			return "W";
		}
	}

}
