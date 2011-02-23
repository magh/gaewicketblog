package org.gaewicketblog.wicket.page;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.gaewicketblog.common.AppEngineHelper;
import org.gaewicketblog.common.DbHelper;
import org.gaewicketblog.common.Util;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.CommentHelper;
import org.gaewicketblog.wicket.application.BlogApplication;
import org.gaewicketblog.wicket.exception.BlogException;
import org.gaewicketblog.wicket.validator.AdminNameValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.Text;

@SuppressWarnings("serial")
public class UpdatePage extends BorderPage {

	private final static Logger log = LoggerFactory.getLogger(UpdatePage.class);

	public UpdatePage(final Comment comment) {
		super();

		final IModel<String> subject = new Model<String>(comment.getSubject());
		final IModel<String> text = new Model<String>(comment.getText().getValue());
		Text noteText = comment.getNote();
		final IModel<String> note = new Model<String>(
				noteText != null ? noteText.getValue() : "");
		final IModel<String> name = new Model<String>(comment.getAuthor());
		final IModel<String> email = new Model<String>(); //TODO is email stored?
		final IModel<String> homepage = new Model<String>(); //TODO is homepage stored?

		Form<String> update = new Form<String>("update") {
			@Override
			protected void onSubmit() {
				try {
					comment.setSubject(subject.getObject());
					comment.setText(new Text(text.getObject()));
					comment.setNote(new Text(note.getObject()));
					comment.setAuthor(name.getObject());
					if(Util.isEmpty(comment.getLink())) {
						comment.setLink(CommentHelper.genUrlPath(subject.getObject()));
						BlogApplication app = (BlogApplication) getApplication();
						String urlPath = CommentHelper.getUrlPath(comment);
						app.mountBlogPage(urlPath, ViewPage.class);
					}
					//TODO set email and homepage
//					comment.setEmail(email.getObject());
//					comment.setHomepage(homepage.getObject());
					DbHelper.merge(comment);
					setResponsePage(new ViewPage(comment.getId()));
				} catch (BlogException e) {
					log.error(e.getMessage(), e);
					error("Duplicate subject!");
				}
			}
		};
		update.add(new TextField<String>("subject", subject)
				.add(StringValidator.maximumLength(100)));
		update.add(new TextArea<String>("message", text).setRequired(true).add(
				StringValidator.lengthBetween(2, 5000)));
		String adminEmail = getString("admin.email");
		String adminName = getString("admin.name");
		update.add(new TextArea<String>("note", note)
				.setVisible(AppEngineHelper.isAdmin(adminEmail)));
		update.add(new TextField<String>("name", name).setRequired(true)
				.add(StringValidator.maximumLength(100))
				.add(new AdminNameValidator(adminName, adminEmail)));
		update.add(new TextField<String>("email", email)
				.add(EmailAddressValidator.getInstance()));
		update.add(new TextField<String>("homepage", homepage)
				.add(StringValidator.maximumLength(200)));
		update.add(new Link<String>("cancel") {
			@Override
			public void onClick() {
				setResponsePage(new ViewPage(comment.getId()));
			}
		});
//		update.add(new FeedbackLabel("feedback", update));
		update.add(new FeedbackPanel("feedback"));
		add(update);
	}

}
