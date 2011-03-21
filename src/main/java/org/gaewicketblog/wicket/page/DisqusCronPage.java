package org.gaewicketblog.wicket.page;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.gaewicketblog.common.DbHelper;
import org.gaewicketblog.common.Util;
import org.gaewicketblog.common.WicketHelper;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.CommentHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisqusCronPage extends WebPage {
	
	public final static String MOUNTPATH = "/disquscron";

	private final static Logger log = LoggerFactory.getLogger(DisqusCronPage.class);

	private final static String DISQUSFORUM = "http://disqus.com/forums/";
	private final static String DISQUSCOUNTJS = "/count.js?q=1";
	private final static String DISQUSJSONBEG = "DISQUSWIDGETS.displayCount(";
	private final static String DISQUSJSONEND = ");";

	// max urls per request
	private final static int MAX_PER_REQUEST = 15;
	private final static int MAX_LENGTH_PER_REQUEST = 1500;

	public DisqusCronPage() {
		String error = "";
		try{
			String shortname = getString("disqus.shortname");
			Map<Long, String> in = new HashMap<Long, String>();
			Map<Long, Comment> commentsMap = DbHelper.getAllCommentsAsMap();
			List<Comment> tomerge = new ArrayList<Comment>();
	        String server = WicketHelper.getAbsolutUrl(this).replaceFirst(MOUNTPATH, "");
	        int urllen = 0;
	        for (Iterator<Entry<Long, Comment>> it = commentsMap.entrySet()
					.iterator(); it.hasNext();) {
				Entry<Long, Comment> entry = it.next();
	        	String url = server + CommentHelper.getUrlPath(entry.getValue());
	        	urllen += url.length();
				in.put(entry.getKey(), url);
				if(in.size() >= MAX_PER_REQUEST || urllen >= MAX_LENGTH_PER_REQUEST || !it.hasNext()) {
					Map<Long, Integer> map = fetchCommentCounts(shortname, in);
					log.info("fetched comment counts: "+map.size());
					for (Entry<Long, Integer> resentry : map.entrySet()) {
						Long uid = resentry.getKey();
						Integer count = resentry.getValue();
						Comment comment = commentsMap.get(uid);
						if(comment.getComments() != count) {
							comment.setComments(count);
							tomerge.add(comment);
						}
					}
					in = new HashMap<Long, String>();
					urllen = 0;
				}
			}
			log.info("Comments to update: "+tomerge.size());
			if(tomerge.size() > 0){
				DbHelper.mergeAll(tomerge);
			}
		}catch(Exception e){
			log.error(e.getMessage(), e);
			error = e.getMessage()+" - "+e;
		}
		add(new MultiLineLabel("result", error));
	}

	public static Map<Long, Integer> fetchCommentCounts(String shortname,
			Map<Long, String> in) {
		StringBuilder sb = new StringBuilder();
		sb.append(DISQUSFORUM);
		sb.append(shortname);
		sb.append(DISQUSCOUNTJS);

		for (Entry<Long, String> entry : in.entrySet()) {
			try {
				long uid = entry.getKey();
				String url = URLEncoder.encode(entry.getValue(), Util.UTF8);
				sb.append("&").append(uid).append("=2,").append(url);
			} catch (UnsupportedEncodingException e) {
				log.error(e.getMessage(), e);
			}
		}

		Map<Long, Integer> map = new HashMap<Long, Integer>();
		try {
			InputStream is = new URL(sb.toString()).openStream();
			String data = Util.readStream(is);
			int beginIndex = data.indexOf(DISQUSJSONBEG)+DISQUSJSONBEG.length();
			int endIndex = data.lastIndexOf(DISQUSJSONEND);
			String json = data.substring(beginIndex, endIndex);
			JSONObject obj = new JSONObject(json);
			JSONArray counts = obj.getJSONArray("counts");
			for (int i = 0; i < counts.length(); i++) {
				JSONObject count = counts.getJSONObject(i);
				long uid = count.getLong("uid");
				int comments = count.getInt("comments");
				map.put(uid, comments);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return map;
	}

}
