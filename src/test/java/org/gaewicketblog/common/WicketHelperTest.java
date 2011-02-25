package org.gaewicketblog.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class WicketHelperTest {

	@Test
	public void testGetCurrentRestfulPath() {
		//
		String path = "issues/wicket:pageMapName/wicket-1";
		String actual = path.replaceFirst("/wicket:pageMapName/wicket-\\d+", "");
		assertEquals("issues", actual);
		//
		path = "issues/wicket:pageMapName/wicket-11";
		actual = path.replaceFirst("/wicket:pageMapName/wicket-\\d+", "");
		assertEquals("issues", actual);
	}

}
