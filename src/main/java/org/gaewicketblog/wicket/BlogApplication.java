package org.gaewicketblog.wicket;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.HttpSessionStore;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.session.ISessionStore;

public class BlogApplication extends WebApplication {

	@Override
	public Class<? extends Page> getHomePage() {
		return ListPage.class;
	}

	@Override
	protected void init() {
		super.init();
		// remove thread monitoring from resource watcher
		getResourceSettings().setResourcePollFrequency(null);
		mountBookmarkablePage("/"+Constants.NEWS_STR, ListPage.class);
		mountBookmarkablePage("/"+Constants.HELP_STR, ListPage.class);
		mountBookmarkablePage("/"+Constants.FAQ_STR, ListPage.class);
		mountBookmarkablePage("/"+Constants.BUGS_STR, ListPage.class);
		mountBookmarkablePage("/"+Constants.FEATURE_STR, ListPage.class);
		mountBookmarkablePage("/"+Constants.ABOUT_STR, ListPage.class);
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
