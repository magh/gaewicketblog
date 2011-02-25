package org.gaewicketblog.wicket.validator;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.gaewicketblog.common.AppEngineHelper;
import org.gaewicketblog.common.Util;
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

	/* 
	 * Checks that value doesn't contain the adminName unless current user email equals adminEmail.
	 * adminName empty: skip all
	 * adminEmail empty: skip admin email check
	 */
	@Override
	protected void onValidate(IValidatable<String> validatable) {
		if (!Util.isEmpty(adminName)) {
			String in = validatable.getValue();
			if (in.toLowerCase().contains(adminName)) {
				if (Util.isEmpty(adminEmail)
						|| !AppEngineHelper.isCurrentUser(adminEmail)) {
					log.warn("Reserved name: "+in);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("adminname", adminName);
					error(validatable, "reservedname.Validator", map);
				}
			}
		}
	}

}
