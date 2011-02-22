package org.gaewicketblog.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class CommentHelperTest {

	@Test
	public void testEscape() {
		String in = "Is this a ‘test’?";
		String escaped = CommentHelper.genUrlPath(in);
		assertEquals("/is_this_a_test", escaped);
	}

}
