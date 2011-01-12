package org.gaewicketblog.wicket;

import java.util.List;

import javax.jdo.PersistenceManager;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.HttpSessionStore;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.session.ISessionStore;
import org.gaewicketblog.common.DbHelper;
import org.gaewicketblog.common.PMF;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.CommentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlogApplication extends WebApplication {

	private final static Logger log = LoggerFactory.getLogger(BlogApplication.class);

	@Override
	public Class<? extends Page> getHomePage() {
		return ListPage.class;
	}

	@Override
	protected void init() {
		super.init();
		// remove thread monitoring from resource watcher
		getResourceSettings().setResourcePollFrequency(null);
//		log.info("mount static resources");
//		mountSharedResource("/favicon.ico", resourceKey);
		log.info("mount list pages");
		mountBookmarkablePage("/"+Constants.NEWS_STR, ListPage.class);
		mountBookmarkablePage("/"+Constants.HELP_STR, ListPage.class);
		mountBookmarkablePage("/"+Constants.FAQ_STR, ListPage.class);
		mountBookmarkablePage("/"+Constants.BUGS_STR, ListPage.class);
		mountBookmarkablePage("/"+Constants.FEATURE_STR, ListPage.class);
		mountBookmarkablePage("/"+Constants.ABOUT_STR, ListPage.class);
		mountBookmarkablePage("/login", LoginPage.class);
		log.info("mount articles");
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			// Constants.ABOUT - Constants.NEWS
			for (int i = -7; i <= -2; i++) {
				List<Comment> comments = DbHelper.getComments(i, pm);
				for (Comment comment : comments) {
					String urlPath = CommentHelper.getUrlPath(comment);
					mountBookmarkablePage(urlPath, ViewPage.class);
				}
			}
		}finally{
			pm.close();
		}
		log.info("init end");
	}

	@Override
	protected ISessionStore newSessionStore() {
		// return super.newSessionStore();
		// return new SecondLevelCacheSessionStore(this, new
		// InMemoryPageStore());
		return new HttpSessionStore(this);
	}

	@Override
	public String getConfigurationType() {
		return Application.DEPLOYMENT;
	}

}
