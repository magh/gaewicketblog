package org.gaewicketblog.wicket;

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
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.gaewicketblog.common.PMF;
import org.gaewicketblog.common.Util;
import org.gaewicketblog.model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.Text;

@SuppressWarnings("serial")
public class AddPage extends BorderPage {

	private final static Logger log = LoggerFactory.getLogger(AddPage.class);
	
	private Random rand = new Random();

	public AddPage(final long id, final String defsubject) {
		super();

		final IModel<String> captcha = new Model<String>();
		final IModel<String> subject = new Model<String>(defsubject);
		final IModel<String> text = new Model<String>();
		final IModel<String> name = new Model<String>();
		final IModel<String> email = new Model<String>();
		final IModel<String> homepage = new Model<String>();
		
		final int a = rand.nextInt(10);
		final int b = rand.nextInt(10);

		Form<String> update = new Form<String>("update") {
			@Override
			protected void onSubmit() {
				HttpServletRequest httpRequest = ((ServletWebRequest) getRequest())
						.getHttpServletRequest();
				String ipaddress = httpRequest.getRemoteAddr();
				PersistenceManager pm = PMF.get().getPersistenceManager();
				Comment comment = new Comment(id, subject.getObject(),
						new Text(text.getObject()), name.getObject(), ipaddress);
				try {
					comment = pm.makePersistent(comment);

					sendEmailToSelf(getString("borderpage.title")+": "
							+ comment.getSubject(), comment.toString());
					log.debug("Added/updated comment!");
					if (Util.isEmpty(defsubject)) {
						setResponsePage(new ListPage(id));
					}else{
						setResponsePage(new ViewPage(id));
					}
				} finally {
					pm.close();
				}
			}
		};
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
		update.add(new TextField<String>("name", name).setRequired(true).add(
				StringValidator.maximumLength(100)).add(
				new AbstractValidator<String>() {
					@Override
					protected void onValidate(IValidatable<String> validatable) {
						String in = validatable.getValue();
						String adminName = getString("admin.name");
						if (in.toLowerCase().contains(adminName)) {
							log.warn("Reserved name: "+in);
							error(validatable, "reservedname.Validator");
						}
					}
				}));
		update.add(new TextField<String>("email", email)
				.add(EmailAddressValidator.getInstance()));
		update.add(new TextField<String>("homepage", homepage)
				.add(StringValidator.maximumLength(200)));
		update.add(new Link<String>("cancel") {
			@Override
			public void onClick() {
				if (Util.isEmpty(defsubject)) {
					setResponsePage(new ListPage(id));
				}else{
					setResponsePage(new ViewPage(id));
				}
			}
		});
//		update.add(new FeedbackLabel("feedback", update));
		update.add(new FeedbackPanel("feedback"));
		add(update);
	}

	public boolean sendEmailToSelf(String subject, String text){
		try{
			String emailSelfEmail = getString("emailself.email");
			String emailSelfName = getString("emailself.name");
			
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
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

}
