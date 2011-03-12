package org.gaewicketblog.model;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class CommentHelperTest {

	@Test
	public void testEscape() {
		String in = "Is this a ‘test’?";
		String escaped = CommentHelper.genUrlPath(in);
		assertEquals("/is_this_a_test", escaped);
		//
		in = "How does “this” work?";
		escaped = CommentHelper.genUrlPath(in);
		assertEquals("/how_does_this_work", escaped);
	}

	@Test
	public void testByStarred() {
		String userId = "userId";
		Comment cmt1 = new Comment(1, "subject", null, "author", "ipaddress", "link");
		cmt1.setStarredIds(Arrays.asList(userId));
		Comment cmt2 = new Comment(2, "subject", null, "author", "ipaddress", "link");
//		cmt2.setStarredIds(Arrays.asList(userId));
		Comment cmt3 = new Comment(3, "subject", null, "author", "ipaddress", "link");
		cmt3.setStarredIds(Arrays.asList(userId));
		Comment cmt4 = new Comment(4, "subject", null, "author", "ipaddress", "link");
//		cmt4.setStarredIds(Arrays.asList(userId));
		List<Comment> list = Arrays.asList(cmt1, cmt2, cmt3, cmt4);
		Collections.sort(list, CommentHelper.byStarred(userId));
		assertEquals(new Long(1), list.get(0).getParentid());
		assertEquals(new Long(3), list.get(1).getParentid());
		assertEquals(new Long(2), list.get(2).getParentid());
		assertEquals(new Long(4), list.get(3).getParentid());
	}

}
