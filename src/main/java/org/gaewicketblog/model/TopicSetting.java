package org.gaewicketblog.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TopicSetting implements Serializable {
	
	public final static TopicSetting NULLSETTING = new TopicSetting(-1, "/",
			false, false, "Invalid topic", "Invalid topic", null);

	public long id;
	public String path;
	public boolean canPost;
	public boolean showText;
	public String topic;
	public String topicdesc;
	public String pageClass;

	public TopicSetting(long id, String path, boolean canPost,
			boolean showText, String topic, String topicdesc, String pageClass) {
		this.id = id;
		this.path = path;
		this.canPost = canPost;
		this.showText = showText;
		this.topic = topic;
		this.topicdesc = topicdesc;
		this.pageClass = pageClass;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[id=").append(id).append("]");
		sb.append("[path=").append(path).append("]");
		sb.append("[canPost=").append(canPost).append("]");
		sb.append("[showText=").append(showText).append("]");
		sb.append("[topic=").append(topic).append("]");
		sb.append("[topicdesc=").append(topicdesc).append("]");
		sb.append("[pageClass=").append(pageClass).append("]");
		return sb.toString();
	}

}
