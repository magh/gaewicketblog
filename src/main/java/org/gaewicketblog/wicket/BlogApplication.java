package org.gaewicketblog.wicket;

import java.util.ArrayList;
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
	
	private List<String> mountedUrls = new ArrayList<String>();

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
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			log.info("mount list pages");
			mountBlogPage("/"+Constants.NEWS_STR, ListPage.class);
			mountBlogPage("/"+Constants.HELP_STR, ListPage.class);
			mountBlogPage("/"+Constants.FAQ_STR, ListPage.class);
//			mountBlogPage("/"+Constants.BUGS_STR, ListPage.class);
			mountBlogPage("/"+Constants.ISSUES_STR, ListPage.class);
			mountBlogPage("/"+Constants.ABOUT_STR, ListPage.class);
			mountBlogPage("/login", LoginPage.class);
			log.info("mount articles");
			// Constants.ABOUT - Constants.NEWS
			for (int i = -7; i <= -2; i++) {
				List<Comment> comments = DbHelper.getComments(i, pm);
				for (Comment comment : comments) {
					String urlPath = CommentHelper.getUrlPath(comment);
					mountBlogPage(urlPath, ViewPage.class);
				}
			}
		} catch (BlogException e) {
			throw new RuntimeException(e);
		}finally{
			pm.close();
		}
		log.info("init end");
	}

	public <T extends Page> void mountBlogPage(String path,
			Class<T> bookmarkablePageClass) throws BlogException {
		if(mountedUrls.contains(path)){
			throw new BlogException("Page already mounted: "+path);
		}
		mountedUrls.add(path);
		mountBookmarkablePage(path, bookmarkablePageClass);
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
