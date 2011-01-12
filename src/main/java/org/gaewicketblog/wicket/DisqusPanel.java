package org.gaewicketblog.wicket;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.gaewicketblog.common.Util;

@SuppressWarnings("serial")
public class DisqusPanel extends Panel {

	public DisqusPanel(String id, String pid, String purl, String ptitle, boolean devmode) {
		super(id);
		StringBuilder sb = new StringBuilder();
		String shortname = getString("disquspanel.shortname");
		if(Util.isEmpty(shortname)){
			if(devmode){
				sb.append("var disqus_developer = 1;\n"); // developer mode is on
			}
			sb.append("var disqus_shortname = '").append(shortname).append("';\n");
			// The following are highly recommended additional parameters.
			if(pid != null){
				sb.append("var disqus_identifier = '").append(pid).append("';\n");
			}
			if(purl != null){
				sb.append("var disqus_url = '").append(purl).append("';\n");
			}
			if(ptitle != null){
				sb.append("var disqus_title = '").append(ptitle).append("';\n");
			}
			// DON'T EDIT BELOW THIS LINE
			sb.append("(function() {\n");
			sb.append("var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;\n");
			sb.append("dsq.src = 'http://' + disqus_shortname + '.disqus.com/embed.js';\n");
			sb.append("(document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);\n");
			sb.append("})();\n");
			// END
		}
		Label disqus = new Label("disqus", sb.toString());
		disqus.setEscapeModelStrings(false); // do not HTML escape JavaScript code
		add(disqus);
	}

}
