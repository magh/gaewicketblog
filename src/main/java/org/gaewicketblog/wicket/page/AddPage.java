package org.gaewicketblog.wicket.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.jdo.PersistenceManager;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

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
import org.gaewicketblog.common.PMF;
import org.gaewicketblog.common.Util;
import org.gaewicketblog.model.Comment;
import org.gaewicketblog.model.CommentHelper;
import org.gaewicketblog.model.TopicSetting;
import org.gaewicketblog.wicket.application.BlogApplication;
import org.gaewicketblog.wicket.exception.BlogException;
import org.gaewicketblog.wicket.validator.AdminNameValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.com.google.common.base.Pair;

@SuppressWarnings("serial")
public class AddPage extends BorderPage {

	private final static Logger log = LoggerFactory.getLogger(AddPage.class);
	
	private Random rand = new Random();

	public AddPage(final TopicSetting parent, String title) {
		super();

		final IModel<String> captcha = new Model<String>();
		final IModel<String> subject = new Model<String>();
		final IModel<String> text = new Model<String>();
		final IModel<String> name = new Model<String>();
		final IModel<String> email = new Model<String>();
		final IModel<String> homepage = new Model<String>();
		final IModel<String> note = new Model<String>();
		final IModel<Pair<Integer, String>> status = new Model<Pair<Integer, String>>();
		final IModel<Integer> votes = new Model<Integer>();
		
		final int a = rand.nextInt(10);
		final int b = rand.nextInt(10);

		UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if(user != null){
        	email.setObject(user.getEmail());
        	name.setObject(user.getNickname());
        }

		Form<String> update = new Form<String>("update") {
			@Override
			protected void onSubmit() {
				HttpServletRequest httpRequest = ((ServletWebRequest) getRequest())
						.getHttpServletRequest();
				String ipaddress = httpRequest.getRemoteAddr();
				PersistenceManager pm = PMF.get().getPersistenceManager();
				String link = CommentHelper.genUrlPath(subject.getObject());
				Comment comment = new Comment(parent.id, subject.getObject(),
						new Text(text.getObject()), name.getObject(), ipaddress, link);
				comment.setEmail(email.getObject());
				comment.setHomepage(homepage.getObject());
				//admin fields
				String noteStr = note.getObject();
				comment.setNote(noteStr != null ? new Text(noteStr) : null);
				comment.setVotes(votes.getObject());
				Pair<Integer, String> statusVal = status.getObject();
				if(statusVal != null){
					comment.setStatus(statusVal.first);
				}
				try {
					BlogApplication app = (BlogApplication) getApplication();
					String urlPath = CommentHelper.getUrlPath(comment);
					app.mountBlogPage(urlPath, ViewPage.class);

					comment = pm.makePersistent(comment);

					String adminEmail = getString("admin.email");
					String adminName = getString("admin.name");
					if (!adminName.equalsIgnoreCase(comment.getAuthor())
							&& !adminEmail.equalsIgnoreCase(email.getObject() /*comment.getEmail()*/)) {
						String emailSelfEmail = getString("emailself.email");
						String emailSelfName = getString("emailself.name");
						sendEmailToSelf(getString("borderpage.title") + ": "
								+ comment.getSubject(), comment.toString(),
								emailSelfName, emailSelfEmail);
					}
					log.debug("Added/updated comment!");
					setResponsePage(new ViewPage(comment.getId()));
				} catch (BlogException e) {
					log.error(e.getMessage(), e);
					error("Duplicate subject!");
				} finally {
					pm.close();
				}
			}
		};
		add(update);

		update.add(new Label("title", title));
		update.add(new Label("captchaq", a+" + "+b+" is? (hint: "+(a+b)+")"));
		update.add(new TextField<String>("captcha", captcha).setRequired(true)
				.add(new AbstractValidator<String>() {
					@Override
					protected void onValidate(IValidatable<String> validatable) {
						String in = validatable.getValue();
						if (a + b != Util.parseInt(in, -1)) {
							error(validatable, "captcha.Validator");
						}
					}
				}));
		update.add(new TextField<String>("subject", subject)
				.add(StringValidator.maximumLength(100)));
		update.add(new TextArea<String>("message", text).setRequired(true).add(
				StringValidator.lengthBetween(2, 5000)));
		String adminEmail = getString("admin.email");
		String adminName = getString("admin.name");
		update.add(new TextField<String>("name", name).setRequired(true)
				.add(StringValidator.maximumLength(100))
				.add(new AdminNameValidator(adminName, adminEmail)));
		update.add(new TextField<String>("email", email).add(
				EmailAddressValidator.getInstance()).add(
				new AdminNameValidator(adminEmail, adminEmail)));
		update.add(new TextField<String>("homepage", homepage)
				.add(StringValidator.maximumLength(200)));
		update.add(new Link<String>("cancel") {
			@Override
			public void onClick() {
				getRequestCycle().setRequestTarget(
						new RedirectRequestTarget("/" + parent.path));
			}
		});
//		update.add(new FeedbackLabel("feedback", update));
		update.add(new FeedbackPanel("feedback"));

		// admin visible fields
		boolean admin = AppEngineHelper.isCurrentUser(adminEmail);
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
		update.add(new DropDownChoice<Pair<Integer, String>>("status", status,
				choices, new ChoiceRenderer<Pair<Integer, String>>("second"))
				.setVisible(admin));
	}

	private Pair<Integer, String> newStatusPair(int status){
		String statusStr = CommentHelper.getStatusString(this, status);
		return new Pair<Integer, String>(status, statusStr);
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
