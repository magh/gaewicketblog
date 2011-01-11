# GaeWicketBlog

Simple blog framework using Wicket on Google App Engine.

## Fork and update the following files:
- ./src/main/webapp/WEB-INF/appengine-web.xml (Mandatory)
- ./src/main/java/org/gaewicketblog/wicket/BorderPage.properties (Optional)
- ./src/main/java/org/gaewicketblog/wicket/ContactPanel.properties (Optional)
- ./src/main/java/org/gaewicketblog/wicket/BlogApplication.properties (Optional)
- ./src/main/java/org/gaewicketblog/wicket/images/icon.png (Optional)
- ./src/main/java/org/gaewicketblog/wicket/images/favicon.ico (Optional)

## Build
### Full update, asks for login credentials.
$APPENGINE_HOME/bin/appcfg.sh update target/war
### Simple update, can be executed after a full update.
ant clean update
### Maven project build (not required before appengine update).
mvn clean package

![Screenshoot](http://magh.github.com/gaewicketblog/screenshots/2011-01-11.png)

