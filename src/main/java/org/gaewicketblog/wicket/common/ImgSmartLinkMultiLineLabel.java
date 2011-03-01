package org.gaewicketblog.wicket.common;

import org.apache.wicket.extensions.markup.html.basic.ILinkParser;
import org.apache.wicket.extensions.markup.html.basic.ILinkRenderStrategy;
import org.apache.wicket.extensions.markup.html.basic.LinkParser;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkMultiLineLabel;

@SuppressWarnings("serial")
public class ImgSmartLinkMultiLineLabel extends SmartLinkMultiLineLabel {

	public ImgSmartLinkMultiLineLabel(String id, String label) {
		super(id, label);
	}

	@Override
	protected ILinkParser getLinkParser() {
		return new SmartLinkParser();
	}

	public static class SmartLinkParser extends LinkParser {

		private static final String urlPattern = "([a-zA-Z]+://[\\w\\.\\-\\:\\/~]+)[\\w\\.:\\-/?&=%]*";

		public static final ILinkRenderStrategy URL_RENDER_STRATEGY = new ILinkRenderStrategy() {
			public String buildLink(String linkTarget) {
				if (linkTarget.endsWith(".png") || linkTarget.endsWith(".jpg")
						|| linkTarget.endsWith(".gif")) {
					return "<img src=\"" + linkTarget + "\" alt=\"" + linkTarget
							+ "\">";
				}
				return "<a href=\""
						+ linkTarget
						+ "\">"
						+ (linkTarget.indexOf('?') == -1 ? linkTarget
								: linkTarget.substring(0,
										linkTarget.indexOf('?'))) + "</a>";
			}
		};

		public SmartLinkParser() {
			super();
			addLinkRenderStrategy(urlPattern, URL_RENDER_STRATEGY);
		}
	}

}
