package org.gaewicketblog.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.gaewicketblog.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommentHelper {
	
	private final static Logger log = LoggerFactory.getLogger(CommentHelper.class);

//	private final static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd ");
	
	private final static String STATUS_PREFIX = "comment.status";
	private final static String STATUS_SUFFIX = ".title";

	public static String getStatusString(Component context, int status){
		return context.getString(CommentHelper.STATUS_PREFIX + status
				+ CommentHelper.STATUS_SUFFIX);
	}

	public static Component newStatusColorLabel(Component context, String id,
			int status) {
		String text = getStatusString(context, status);
		return new Label(id, text).add(new AttributeModifier("class", true,
				new Model<String>("status "
						+ CommentHelper.getStatusColorClass(status))));
	}

	private static String getStatusColorClass(int status){
		switch(status){
		case Comment.STATUS_UNASSIGNED:
			return "grey";
		case Comment.STATUS_OPEN_NEEDSINFO:
			return "orange";
		case Comment.STATUS_OPEN_UNDERREVIEW:
			return "purple";
		case Comment.STATUS_OPEN_STARTED:
			return "magenta";
		case Comment.STATUS_OPEN_PENDING:
			return "green";
		case Comment.STATUS_CLOSED_COMPLETED:
			return "blue";
		case Comment.STATUS_CLOSED_DECLINED:
			return "red";
		case Comment.STATUS_CLOSED_DUPLICATE:
			return "black";
		}
		log.warn("getStatusColorClass: Invalid status="+status);
		return "white";
	}

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

	public static Comparator<Comment> byComments = new Comparator<Comment>() {
		@Override
		public int compare(Comment arg0, Comment arg1) {
			if (arg0 != null && arg1 != null) {
				return Integer.valueOf(arg0.getComments()).compareTo(arg1.getComments());
			}
			return -1;
		}
	};

	public static Comparator<Comment> byStatus = new Comparator<Comment>() {
		@Override
		public int compare(Comment arg0, Comment arg1) {
			if (arg0 != null && arg1 != null) {
				return Integer.valueOf(arg0.getStatus()).compareTo(arg1.getStatus());
			}
			return -1;
		}
	};

	public static Comparator<Comment> byVotes = new Comparator<Comment>() {
		@Override
		public int compare(Comment arg0, Comment arg1) {
			if (arg0 != null && arg1 != null) {
				return Integer.valueOf(arg0.getVotes()).compareTo(arg1.getVotes());
			}
			return -1;
		}
	};

	public static Comparator<Comment> byType = new Comparator<Comment>() {
		@Override
		public int compare(Comment arg0, Comment arg1) {
			if (arg0 != null && arg1 != null) {
				return Integer.valueOf(arg0.getType()).compareTo(arg1.getType());
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

	/**
	 * @param comments
	 * @param in
	 * @param statuses to null searches all
	 * @return
	 */
	public static List<Comment> search(List<Comment> comments, String in,
			int[] statuses) {
		List<Comment> found = new ArrayList<Comment>();
		for (Comment comment : comments) {
			if(statuses == null || Util.contains(comment.getStatus(), statuses)) {
				if (Util.isEmpty(in) || comment.getAuthor().toLowerCase().contains(in)
						|| comment.getSubject().toLowerCase().contains(in)
						|| comment.getText().getValue().toLowerCase().contains(in)) {
					found.add(comment);
				}
			}
		}
		return found;
	}

}
