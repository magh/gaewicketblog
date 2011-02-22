package org.gaewicketblog.model;

import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.gaewicketblog.common.Util;
import org.gaewicketblog.wicket.page.ListPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicSettingHelper {

	private final static Logger log = LoggerFactory
			.getLogger(TopicSettingHelper.class);

	public static TopicSetting getByPath(List<TopicSetting> topics, String path) {
		for (TopicSetting topic : topics) {
			if (path.endsWith(topic.path)) {
				return topic;
			}
		}
		return topics.size() > 0 ? topics.get(0) : TopicSetting.NULLSETTING;
	}

	public static TopicSetting getById(List<TopicSetting> topics, long id) {
		for (TopicSetting topic : topics) {
			if (id == topic.id) {
				return topic;
			}
		}
		return topics.size() > 0 ? topics.get(0) : TopicSetting.NULLSETTING;
	}

	public static Class<? extends WebPage> getPageClass(String pageClassStr){
		Class<? extends WebPage> pageClass = ListPage.class;
		if(!Util.isEmpty(pageClassStr)){
			try {
				pageClass = (Class<? extends WebPage>) Class
						.forName(pageClassStr);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return pageClass;
	}

}
