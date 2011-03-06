## GaeWicketBlog ##

# General
GaeWicketBlog is a simple open source blog engine running on the free hosting service Google App Engine. GaeWicketBlog is built using the Java web framework Wicket (http://wicket.apache.org).

Installation is fairly simple and should not require any Wicket specific knowledge.
The appengine SDK (http://code.google.com/appengine) is required for deployment.
The version control system git (http://git-scm.com/download) is required to retrieve the source code.
 
# Installation
Clone source code:
git clone git@github.com:magh/gaewicketblog.git

Update the appengine file with your application and version identifiers (mandatory):
src/main/webapp/WEB-INF/appengine-web.xml 

Update the gwb properties with your blog specific information (optional):
src/main/java/org/gaewicketblog/wicket/application/BlogApplication.properties 

Update the blog logo (optional):
src/main/java/org/gaewicketblog/wicket/images/icon.png 

# Deploy
gaewicketblog> appcfg.sh update target/war

# Development
Feel free to fork the project on github. Design improvments, bug reports, feature requests are very welcomed. Issues can be posted here (http://gaewicketblog.appspot.com/issues) or on the github issue tracker. 

Eclipse project files can be generated with maven:
gaewicketblog> mvn eclipse:eclipse

![Screenshoot](http://magh.github.com/gaewicketblog/screenshots/post.png)

