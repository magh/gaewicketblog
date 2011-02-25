package org.gaewicketblog.common;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WicketHelper {

	private final static Logger log = LoggerFactory
			.getLogger(WicketHelper.class);

	public static String getCurrentRestfulPath() {
		String path = RequestCycle.get().getRequest().getPath();
		//TODO just return path.replaceFirst...
		if (path.contains("pageMapName")) {
			// remove any /wicket:pageMapName/wicket-X
			String res = path.replaceFirst("/wicket:pageMapName/wicket-\\d+", "");
			log.warn("getCurrentRestfulPath out=" + res + " in=" + path);
			return res;
		}
		return path;
	}

	public static String getAbsolutUrl(Component context) {
		HttpServletRequest httpRequest = ((ServletWebRequest) context
				.getRequest()).getHttpServletRequest();
		return httpRequest.getRequestURL().toString();
	}

}
