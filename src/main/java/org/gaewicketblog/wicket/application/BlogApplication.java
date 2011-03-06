package org.gaewicketblog.wicket.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jdo.PersistenceManager;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.HttpSessionStore;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.session.ISessionStore;
import org.gaewicketblog.common.DbHelper;
import org.gaewicketblog.common.PMF;
import org.gaewicketblog.common.Util;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.CommentHelper;
import org.gaewicketblog.model.TopicSetting;
import org.gaewicketblog.model.TopicSettingHelper;
import org.gaewicketblog.wicket.exception.BlogException;
import org.gaewicketblog.wicket.page.DisqusCronPage;
import org.gaewicketblog.wicket.page.HomePage;
import org.gaewicketblog.wicket.page.LoginPage;
import org.gaewicketblog.wicket.page.ViewPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlogApplication extends WebApplication {

	private final static Logger log = LoggerFactory.getLogger(BlogApplication.class);

	private List<String> mountedUrls = new ArrayList<String>();

	public List<TopicSetting> topics = new ArrayList<TopicSetting>();

	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}

	@Override
	protected void init() {
		super.init();

		// remove thread monitoring from resource watcher
		getResourceSettings().setResourcePollFrequency(null);

		// to avoid /wicket:pageMapName/wicket-X being added (on session timeout?).
		// TODO should path resolver be improved instead? Tried but didn't work... Trying with multi window support off.
		getPageSettings().setAutomaticMultiWindowSupport(false);

//		log.debug("mount static resources");
//		mountSharedResource("/favicon.ico", resourceKey);

		log.debug("load header menus/topics");
		loadHeaderMenus();

		log.debug("mount pages");
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			mountBlogPage(DisqusCronPage.MOUNTPATH, DisqusCronPage.class);
			mountBlogPage(LoginPage.MOUNTPATH, LoginPage.class);
			for (TopicSetting topic : topics) {
				log.debug("mount pages for: "+topic.topic);
				Class<? extends WebPage> pageClass = TopicSettingHelper
						.getPageClass(topic.pageClass);
				mountBlogPage("/"+topic.path, pageClass);
				List<Comment> comments = DbHelper.getComments(topic.id, pm);
				for (Comment comment : comments) {
					String urlPath = CommentHelper.getUrlPath(comment);
					//TODO check all topic paths, not just current
					if(!topic.path.equals(urlPath)){
						mountBlogPage(urlPath, ViewPage.class);
					}
				}
			}
		} catch (BlogException e) {
			throw new RuntimeException(e);
		} finally {
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
	
	private void loadHeaderMenus(){
//		getResourceSettings().setThrowExceptionOnMissingResource(false);
		InputStream is = BlogApplication.class.getResourceAsStream("BlogApplication.properties");
		Properties props = new Properties();
		try {
			props.load(is);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		for (int i = 0; i < 10; i++) {
			try{
				long id = Long.parseLong(props.getProperty("header.menu"+i+".id"));
				String path = props.getProperty("header.menu"+i+".path");
				boolean canPost = Util.parseBool(props.getProperty("header.menu"+i+".canpost"), false);
				boolean showText = Util.parseBool(props.getProperty("header.menu"+i+".showtext"), true);
				String topic = props.getProperty("header.menu"+i+".title");
				String topicdesc = props.getProperty("header.menu"+i+".description");
				String clz = props.getProperty("header.menu"+i+".class");
				topics.add(new TopicSetting(id, path, canPost, showText, topic, topicdesc, clz));
			}catch(Exception e){
				log.debug("loadHeaderMenus: " + i + " - " + e.getMessage());
			}
		}
		log.info("Topics found: "+topics.size());
	}

}
