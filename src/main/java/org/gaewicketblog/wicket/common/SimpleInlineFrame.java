package org.gaewicketblog.wicket.common;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.util.string.Strings;

@SuppressWarnings("serial")
public class SimpleInlineFrame extends WebMarkupContainer {

	private String url;
	private int width;
	private int height;

	public SimpleInlineFrame(final String id, String url, int width, int height) {
		super(id);
		this.url = url;
		this.width = width;
		this.height = height;
	}

	@Override
	protected final void onComponentTag(final ComponentTag tag) {
		checkComponentTag(tag, "iframe");
		tag.put("src", Strings.replaceAll(url, "&", "&amp;"));
		tag.put("width", ""+width);
		tag.put("height", ""+height);
		tag.put("style", "border: none; overflow: hidden; width: " + width
				+ "px; height: " + height + "px;");
		super.onComponentTag(tag);
	}

	@Override
	protected boolean getStatelessHint() {
		return false;
	}

}
