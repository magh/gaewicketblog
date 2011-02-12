package org.gaewicketblog.wicket;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

	public List<TopicSetting> topics = new ArrayList<TopicSetting>();

	@Override
	public Class<? extends Page> getHomePage() {
		return ListPage.class;
	}

	@Override
	protected void init() {
		super.init();

		// remove thread monitoring from resource watcher
		getResourceSettings().setResourcePollFrequency(null);

//		log.debug("mount static resources");
//		mountSharedResource("/favicon.ico", resourceKey);

		log.debug("load header menus/topics");
		loadHeaderMenus();

		log.debug("mount pages");
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			mountBlogPage("/login", LoginPage.class);
			for (TopicSetting topic : topics) {
				log.debug("mount pages for: "+topic.topic);
				mountBlogPage("/"+topic.path, ListPage.class);
				List<Comment> comments = DbHelper.getComments(topic.id, pm);
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
		try{
			for (int i = 1; i < 10; i++) {
				long id = Long.parseLong(props.getProperty("header.menu"+i+".id"));
				String path = props.getProperty("header.menu"+i+".path");
				boolean canPost = Boolean.parseBoolean(props.getProperty("header.menu"+i+".canpost"));
				boolean showText = Boolean.parseBoolean(props.getProperty("header.menu"+i+".showtext"));
				String topic = props.getProperty("header.menu"+i+".title");
				String topicdesc = props.getProperty("header.menu"+i+".description");
				topics.add(new TopicSetting(id, path, canPost, showText, topic, topicdesc));
			}
		}catch(Exception e){
			log.info("Topics found: "+topics.size());
		}
	}

}
