package org.gaewicketblog.wicket.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.gaewicketblog.common.AppEngineHelper;
import org.gaewicketblog.common.DbHelper;
import org.gaewicketblog.common.Util;
import org.gaewicketblog.common.XssUtil;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.CommentHelper;
import org.gaewicketblog.model.TopicSetting;
import org.gaewicketblog.wicket.application.BlogApplication;
import org.gaewicketblog.wicket.exception.BlogException;
import org.gaewicketblog.wicket.validator.AdminNameValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.settings.TinyMCESettings;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.com.google.common.base.Pair;

@SuppressWarnings("serial")
public class EditPage extends BorderPage {

	private final static Logger log = LoggerFactory.getLogger(EditPage.class);
	
	private Random rand = new Random();

	/**
	 * Add view
	 * @param addParent
	 * @param title
	 */
	public EditPage(String title, TopicSetting addParent) {
		this(addParent, title, null);
	}

	/**
	 * Update view
	 * @param updateComment
	 * @param title
	 */
	public EditPage(String title, Comment updateComment) {
		this(null, title, updateComment);
	}

	/**
	 * @param addParent
	 * @param title
	 * @param updateComment if update or add page.
	 */
	private EditPage(final TopicSetting addParent, String title,
			final Comment updateComment) {
		super();
		final boolean update = updateComment != null;

		final IModel<String> captcha = new Model<String>();
		final IModel<String> subject = new Model<String>();
		final IModel<String> text = new Model<String>();
		final IModel<String> note = new Model<String>();
		final IModel<String> name = new Model<String>();
		final IModel<String> email = new Model<String>();
		final IModel<String> homepage = new Model<String>();
		final IModel<Pair<Integer, String>> status = new Model<Pair<Integer, String>>();
		final IModel<Integer> votes = new Model<Integer>();
		
		UserService userService = UserServiceFactory.getUserService();
        final User currentUser = userService.getCurrentUser();

        if(update) { // update
			subject.setObject(updateComment.getSubject());
			text.setObject(updateComment.getText().getValue());
			Text noteText = updateComment.getNote();
			note.setObject(noteText != null ? noteText.getValue() : "");
			name.setObject(updateComment.getAuthor());
			email.setObject(updateComment.getEmail());
			homepage.setObject(updateComment.getHomepage());
			votes.setObject(updateComment.getVotes());
			status.setObject(newStatusPair(updateComment != null ? updateComment
					.getStatus() : Comment.STATUS_UNASSIGNED));
		}else{ // add
	        if(currentUser != null){
	        	email.setObject(currentUser.getEmail());
	        	name.setObject(currentUser.getNickname());
	        }
		}
		
		Form<String> editform = new Form<String>("editform") {
			@Override
			protected void onSubmit() {
				try {
					String scrubbed = XssUtil.scrub(text.getObject());
					Comment newComment;
					if(update){
						// update
						newComment = updateComment;
						newComment.setSubject(subject.getObject());
						newComment.setText(new Text(scrubbed));
						newComment.setAuthor(name.getObject());
					}else{
						// add
						String ipaddress = getIpAddress();
						newComment = new Comment(addParent.id, subject.getObject(),
								new Text(scrubbed), name.getObject(), ipaddress, null);
					}
					newComment.setEmail(email.getObject());
					newComment.setHomepage(homepage.getObject());
					String noteStr = XssUtil.scrub(note.getObject());
					newComment.setNote(noteStr != null ? new Text(noteStr) : null);
					newComment.setVotes(votes.getObject());
					Pair<Integer, String> statusVal = status.getObject();
					if(statusVal != null){
						newComment.setStatus(statusVal.first);
					}
					// mount if add or link not set
					if(Util.isEmpty(newComment.getLink())) {
						newComment.setLink(CommentHelper.genUrlPath(subject.getObject()));
						BlogApplication app = (BlogApplication) getApplication();
						String urlPath = CommentHelper.getUrlPath(newComment);
						app.mountBlogPage(urlPath, ViewPage.class);
					}
					// DB add/update
					newComment = DbHelper.merge(newComment);

					String adminEmail = getString("admin.email");
					String adminName = getString("admin.name");
					//check if email self
					if (!adminName.equalsIgnoreCase(newComment.getAuthor())
							&& !adminEmail.equalsIgnoreCase(newComment.getEmail())
							&& (currentUser == null || !adminEmail
									.equalsIgnoreCase(currentUser.getEmail()))) {
						String emailSelfEmail = getString("emailself.email");
						String emailSelfName = getString("emailself.name");
						sendEmailToSelf(getString("borderpage.title") + ": "
								+ newComment.getSubject(), newComment.toString(),
								emailSelfName, emailSelfEmail);
					}
					log.debug("Added/updated comment!");
					setResponsePage(new ViewPage(newComment.getId()));
				} catch (BlogException e) {
					log.error(e.getMessage(), e);
					error("Duplicate subject!");
				}
			}
		};
		add(editform);

		TinyMCESettings settings = new TinyMCESettings();
		settings.addCustomSetting("apply_source_formatting : false");

		//add
		final int a = rand.nextInt(10);
		final int b = rand.nextInt(10);
		editform.add(new Label("title", title));
		WebMarkupContainer captchatr = new WebMarkupContainer("captchatr");
		captchatr.add(new Label("captchaq", a+" + "+b+" is? (hint: "+(a+b)+")"));
		captchatr.add(new TextField<String>("captcha", captcha).setRequired(!update)
				.add(new AbstractValidator<String>() {
					@Override
					protected void onValidate(IValidatable<String> validatable) {
						String in = validatable.getValue();
						if (a + b != Util.parseInt(in, -1)) {
							error(validatable, "captcha.Validator");
						}
					}
				}));
		editform.add(captchatr.setVisible(!update));
		String adminEmail = getString("admin.email");
		String adminName = getString("admin.name");
		editform.add(new TextField<String>("name", name).setRequired(true)
				.add(StringValidator.maximumLength(100))
				.add(new AdminNameValidator(adminName, adminEmail)));
		//update
		editform.add(new TextField<String>("subject", subject)
				.add(StringValidator.maximumLength(100)));
		editform.add(new TextArea<String>("message", text).setRequired(true).add(
				StringValidator.lengthBetween(2, 7000)).add(
				new TinyMceBehavior(settings)));
		editform.add(new TextField<String>("email", email).add(
				EmailAddressValidator.getInstance()).add(
				new AdminNameValidator(adminEmail, adminEmail)));
		editform.add(new TextField<String>("homepage", homepage)
				.add(StringValidator.maximumLength(200)));
		editform.add(new Link<String>("cancel") {
			@Override
			public void onClick() {
				if(updateComment != null){
					setResponsePage(new ViewPage(updateComment.getId()));
				}else{
					getRequestCycle().setRequestTarget(
							new RedirectRequestTarget("/" + addParent.path));
				}
			}
		});
//		update.add(new FeedbackLabel("feedback", update));
		editform.add(new FeedbackPanel("feedback"));

		// admin visible fields
		boolean admin = AppEngineHelper.isCurrentUser(adminEmail);
		WebMarkupContainer adminfields = new WebMarkupContainer("adminfields");
		editform.add(adminfields.setVisible(admin));
		adminfields.add(new TextArea<String>("note", note).add(
				StringValidator.maximumLength(7000)).add(new TinyMceBehavior(settings)));
		adminfields.add(new TextField<Integer>("votes", votes, Integer.class));
		List<Pair<Integer, String>> choices = new ArrayList<Pair<Integer,String>>();
		choices.add(newStatusPair(Comment.STATUS_UNASSIGNED));
		choices.add(newStatusPair(Comment.STATUS_OPEN_NEEDSINFO));
		choices.add(newStatusPair(Comment.STATUS_OPEN_UNDERREVIEW));
		choices.add(newStatusPair(Comment.STATUS_OPEN_STARTED));
		choices.add(newStatusPair(Comment.STATUS_OPEN_PENDING));
		choices.add(newStatusPair(Comment.STATUS_CLOSED_COMPLETED));
		choices.add(newStatusPair(Comment.STATUS_CLOSED_DECLINED));
		choices.add(newStatusPair(Comment.STATUS_CLOSED_DUPLICATE));
		adminfields.add(new DropDownChoice<Pair<Integer, String>>("status", status,
				choices, new ChoiceRenderer<Pair<Integer, String>>("second")));
	}

	private Pair<Integer, String> newStatusPair(int status) {
		String statusStr = CommentHelper.getStatusString(this, status);
		return new Pair<Integer, String>(status, statusStr);
	}

	public String getIpAddress() {
		HttpServletRequest httpRequest = ((ServletWebRequest) getRequest())
				.getHttpServletRequest();
		return httpRequest.getRemoteAddr();
	}

	public static boolean sendEmailToSelf(String subject, String text,
			String emailSelfName, String emailSelfEmail) {
		try {
			if(!Util.isEmpty(emailSelfEmail)){
				InternetAddress emailSelf = new InternetAddress(emailSelfEmail, emailSelfName);

				Properties props = new Properties();
				Session session = Session.getDefaultInstance(props,
						null);
				Message msg = new MimeMessage(session);
				msg.setFrom(emailSelf);
				msg.addRecipient(Message.RecipientType.TO,
						emailSelf);
				msg.setSubject(subject);
				msg.setText(text);
				log.info("Sending email from: "+emailSelf.getAddress());
				Transport.send(msg);
				return true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

}
