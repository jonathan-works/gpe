package br.com.infox.validator;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@org.jboss.seam.annotations.faces.Validator(id=FutureDateValidator.NAME)
@Name(FutureDateValidator.NAME)
@BypassInterceptors
public class FutureDateValidator implements Validator {

	static final String NAME = "futureDateValidator";

    public void validate(FacesContext context, UIComponent component, Object value) {
		
		Date date = (Date) value;
		Date now = new Date();
		if (date != null && date.before(now)) {
			throw new ValidatorException(new FacesMessage("#{messages['validator.Date.FUTURE']}"));
		}
		
	}

}
