package org.gaewicketblog.wicket;

import java.util.List;

public class TopicSettingHelper {

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

}
