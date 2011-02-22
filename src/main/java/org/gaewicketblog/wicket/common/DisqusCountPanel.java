package org.gaewicketblog.wicket.common;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.gaewicketblog.common.Util;

@SuppressWarnings("serial")
public class DisqusCountPanel extends Panel {

	public DisqusCountPanel(String id, String shortname) {
		super(id);
		StringBuilder sb = new StringBuilder();
		if(!Util.isEmpty(shortname)){
			if(DisqusPanel.DEVELOPER){
				sb.append("var disqus_developer = 1;\n"); // developer mode is on
			}
			sb.append("var disqus_shortname = '").append(shortname).append("';\n");
			// DON'T EDIT BELOW THIS LINE
			sb.append("(function() {\n");
			sb.append("var s = document.createElement('script'); s.async = true;\n");
			sb.append("s.type = 'text/javascript';\n");
			sb.append("s.src = 'http://' + disqus_shortname + '.disqus.com/count.js';\n");
			sb.append("(document.getElementsByTagName('HEAD')[0] || document.getElementsByTagName('BODY')[0]).appendChild(s);\n");
			sb.append("})();\n");
			// END
		}
		Label disqus = new Label("disqus", sb.toString());
		disqus.setEscapeModelStrings(false); // do not HTML escape JavaScript code
		add(disqus);
	}

}
