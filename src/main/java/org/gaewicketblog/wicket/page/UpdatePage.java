package org.gaewicketblog.wicket.page;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
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
import com.google.appengine.repackaged.com.google.common.base.Pair;

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
		final IModel<Pair<Integer, String>> status = new Model<Pair<Integer, String>>();
		final IModel<Integer> votes = new Model<Integer>(comment.getVotes());

		Form<String> update = new Form<String>("update") {
			@Override
			protected void onSubmit() {
				try {
					comment.setSubject(subject.getObject());
					comment.setText(new Text(text.getObject()));
					comment.setAuthor(name.getObject());
					comment.setEmail(email.getObject());
					comment.setHomepage(homepage.getObject());
					String noteStr = note.getObject();
					comment.setNote(noteStr != null ? new Text(noteStr) : null);
					comment.setVotes(votes.getObject());
					comment.setStatus(status.getObject().first);
					if(Util.isEmpty(comment.getLink())) {
						comment.setLink(CommentHelper.genUrlPath(subject.getObject()));
						BlogApplication app = (BlogApplication) getApplication();
						String urlPath = CommentHelper.getUrlPath(comment);
						app.mountBlogPage(urlPath, ViewPage.class);
					}
					DbHelper.merge(comment);
					setResponsePage(new ViewPage(comment.getId()));
				} catch (BlogException e) {
					log.error(e.getMessage(), e);
					error("Duplicate subject!");
				}
			}
		};
		add(update);

		update.add(new TextField<String>("subject", subject)
				.add(StringValidator.maximumLength(100)));
		update.add(new TextArea<String>("message", text).setRequired(true).add(
				StringValidator.lengthBetween(2, 5000)));
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

		// admin visible fields
		String adminEmail = getString("admin.email");
		String adminName = getString("admin.name");
		boolean admin = AppEngineHelper.isCurrentUser(adminEmail);
		update.add(new TextField<String>("name", name).setRequired(true).add(
				StringValidator.maximumLength(100)).add(
				new AdminNameValidator(adminName, adminEmail))
				.setVisible(admin));
		update.add(new TextArea<String>("note", note).setVisible(admin));
		update.add(new TextField<Integer>("votes", votes, Integer.class)
				.setVisible(admin));
		List<Pair<Integer, String>> choices = new ArrayList<Pair<Integer,String>>();
		choices.add(newStatusPair(Comment.STATUS_UNASSIGNED));
		choices.add(newStatusPair(Comment.STATUS_OPEN_NEEDSINFO));
		choices.add(newStatusPair(Comment.STATUS_OPEN_UNDERREVIEW));
		choices.add(newStatusPair(Comment.STATUS_OPEN_STARTED));
		choices.add(newStatusPair(Comment.STATUS_CLOSED_PENDING));
		choices.add(newStatusPair(Comment.STATUS_CLOSED_COMPLETED));
		choices.add(newStatusPair(Comment.STATUS_CLOSED_DECLINED));
		choices.add(newStatusPair(Comment.STATUS_CLOSED_DUPLICATE));
		status.setObject(newStatusPair(comment.getStatus()));
		update.add(new DropDownChoice<Pair<Integer, String>>("status", status,
				choices, new ChoiceRenderer<Pair<Integer, String>>("second"))
				.setVisible(admin));
	}

	private Pair<Integer, String> newStatusPair(int status) {
		String statusStr = CommentHelper.getStatusString(this, status);
		return new Pair<Integer, String>(status, statusStr);
	}

}
