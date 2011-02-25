package org.gaewicketblog.wicket.page;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class DisqusCronPageTest {

	@Test
	public void testDisqusResultParsing() {
		String json = "{\"showReactions\": false, \"text\": {\"and\": \"and\", \"reactions\": {\"zero\": \"0 Reactions\", \"multiple\": \"{num} Reactions\", \"one\": \"1 Reaction\"}, \"comments\": {\"zero\": \"0 Comments\", \"multiple\": \"{num} Comments\", \"one\": \"1 Comment\"}}, \"counts\": [{\"uid\": 0, \"comments\": 1}]}";
		try {
			JSONObject obj = new JSONObject(json);
			JSONArray counts = obj.getJSONArray("counts");
			JSONObject count0 = counts.getJSONObject(0);
			int uid = count0.getInt("uid");
			assertEquals(0, uid);
			int res = count0.getInt("comments");
			assertEquals(1, res);
		} catch (JSONException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testDisqusFetch() {
//		String shortname = "shortname";
//		String url = "http://example.com/post";
//		int expected = 1;
//		//
//		Map<Long, String> in = new HashMap<Long, String>();
//		long uid = 0;
//		in.put(uid, url);
//		Map<Long, Integer> res = DisqusCronPage.fetchCommentCounts(shortname, in);
//		int count = res.get(uid);
//		assertEquals(expected, count);
	}

}
