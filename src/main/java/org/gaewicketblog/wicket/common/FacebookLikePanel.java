package org.gaewicketblog.wicket.common;

import org.apache.wicket.markup.html.panel.Panel;
import org.gaewicketblog.wicket.common.SimpleInlineFrame;

@SuppressWarnings("serial")
public class FacebookLikePanel extends Panel {

	public FacebookLikePanel(String id, String url) {
		super(id);

		int width = 224;
//		int height = 62;
		int height = 300;
		String colorscheme = "light";
		boolean show_faces = true;
		boolean stream = false;
		boolean header = true;

		StringBuilder sb = new StringBuilder();
		sb.append("http://www.facebook.com/plugins/likebox.php?href=").append(url);
		sb.append("&width=").append(width);
		sb.append("&colorscheme=").append(colorscheme);
		sb.append("&show_faces=").append(show_faces);
		sb.append("&stream=").append(stream);
		sb.append("&header=").append(header);
		sb.append("&height=").append(height);

		add(new SimpleInlineFrame("frame", sb.toString(), width, height));
	}

}
