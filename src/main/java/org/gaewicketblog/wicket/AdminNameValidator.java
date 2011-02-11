package org.gaewicketblog.wicket;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.gaewicketblog.common.AppEngineHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class AdminNameValidator extends AbstractValidator<String> {

	private final static Logger log = LoggerFactory.getLogger(AdminNameValidator.class);
	
	private String adminName;

	private String adminEmail;
	
	public AdminNameValidator(String adminName, String adminEmail) {
		this.adminName = adminName;
		this.adminEmail = adminEmail;
	}

	@Override
	protected void onValidate(IValidatable<String> validatable) {
		if(!AppEngineHelper.isAdmin(adminEmail)){
			String in = validatable.getValue();
			if (in.toLowerCase().contains(adminName)) {
				log.warn("Reserved name: "+in);
				error(validatable, "reservedname.Validator");
			}
		}
	}

}
