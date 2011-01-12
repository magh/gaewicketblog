package org.gaewicketblog.wicket;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.gaewicketblog.common.DbHelper;
import org.gaewicketblog.model.Comment;
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
		final IModel<String> name = new Model<String>(comment.getAuthor());
		final IModel<String> email = new Model<String>(); //TODO is email stored?
		final IModel<String> homepage = new Model<String>(); //TODO is homepage stored?

		Form<String> update = new Form<String>("update") {
			@Override
			protected void onSubmit() {
				comment.setSubject(subject.getObject());
				comment.setText(new Text(text.getObject()));
				comment.setAuthor(name.getObject());
				//TODO
//				comment.setEmail(email.getObject());
//				comment.setHomepage(homepage.getObject());
				DbHelper.merge(comment);
				setResponsePage(new ViewPage(comment.getId()));
			}
		};
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
				setResponsePage(new ListPage(comment.getParentid()));
			}
		});
//		update.add(new FeedbackLabel("feedback", update));
		update.add(new FeedbackPanel("feedback"));
		add(update);
	}

}