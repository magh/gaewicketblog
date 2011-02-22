package org.gaewicketblog.wicket.common;

import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class ImgSmartLinkMultiLineLabelTest {

	@Test
	public void testNewLinkParser() {
		String work = "abcde fbcdg";
		String pattern = "b(c)d";

		Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
		Matcher matcher = p.matcher(work);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			String str = matcher.group(1);
			matcher.appendReplacement(buffer, "BEG"+str+"END");
		}
		matcher.appendTail(buffer);

		assertEquals("aBEGcENDe fBEGcENDg", buffer.toString());
	}

	@Test
	public void testSmartLinkParser() {
		ImgSmartLinkMultiLineLabel.SmartLinkParser parser = new ImgSmartLinkMultiLineLabel.SmartLinkParser();
		//
		String exp = "http://example.com/test.png";
		String res = parser.parse("this is a "+exp+" image.");
		assertEquals("this is a <img src=\""+exp+"\" alt=\""+exp+"\"> image.", res);
	}

}
