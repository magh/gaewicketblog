package org.gaewicketblog.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class XssUtilTest {

	@Test
	public void testOnClick() {
		String in = "onClick=\"alert('fail');\"";
		String expected = "&quot;onClick&quot;=\"alert('fail');\"";
		String actual = XssUtil.scrub(in);
		assertEquals(expected, actual);
	}

	@Test
	public void testScript() {
		String in = "<script type=\"text/javascript\">alert('fail');</script>";
		String expected = "<&quot;script&quot; type=\"text/java&quot;script&quot;\">alert('fail');</&quot;script&quot;>";
		String actual = XssUtil.scrub(in);
		assertEquals(expected, actual);
	}

	@Test
	public void testIFrame() {
		String in = "<IFRAME src=\"http://bad.com\"><iframe>";
		String expected = "<I&quot;frame&quot; src=\"http://bad.com\"><i&quot;frame&quot;>";
		String actual = XssUtil.scrub(in);
		assertEquals(expected, actual);
	}

	@Test
	public void testImgScript(){
		String in = "<IMG \"\"\"><SCRIPT>alert(\"XSS\")</SCRIPT>\">";
		String expected = "<IMG \"\"\"><&quot;script&quot;>alert(\"XSS\")</&quot;script&quot;>\">";
		String actual = XssUtil.scrub(in);
		assertEquals(expected, actual);
	}

}
