package org.gaewicketblog.wicket.provider;

import java.io.Serializable;

public interface ICommentProvider extends Serializable {

	public static final String SORT_DATE = "date";
	public static final String SORT_ID = "id";
	public static final String SORT_TEXT = "text";
	public static final String SORT_SUBJECT = "subject";
	public static final String SORT_AUTHOR = "author";
	public static final String SORT_STATUS = "status";
	public static final String SORT_VOTES = "votes";
	public static final String SORT_TYPE = "type";
	public static final String SORT_COMMENTS = "comments";

}
