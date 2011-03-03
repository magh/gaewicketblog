package org.gaewicketblog.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XssUtil {

	/**
	 * words to quote to try and prevent XSS or similar
	 * words already covered by blacklist: vbscript
	 * TODO change to white list? There are probably multiple 
	 * injection attacks that this doesn't cover but...
	 */
	private static final String[] blacklist = { "script", "style", "frame",
			"onBlur", "onChange", "onClick", "onFocus", "onLoad",
			"onMouseOver", "onSelect", "onSubmit", "onUnload" };

	public static String scrub(String text) {
		if(Util.isEmpty(text)){
			return text;
		}
		for (String w : blacklist) {
			Pattern p = Pattern.compile(w, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(text);
			text = m.replaceAll("&quot;"+w+"&quot;");
		}
		return text;
	}

}
