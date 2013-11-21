package br.com.infox.core.validator;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.international.Messages;

@org.jboss.seam.annotations.faces.Validator(id=PastDateValidator.NAME)
@Name(PastDateValidator.NAME)
@BypassInterceptors
public class PastDateValidator implements Validator {

	public static final String NAME = "pastDateValidator";

    public void validate(FacesContext context, UIComponent component, Object value) {
		
		Date date = (Date) value;
		Date now = new Date();
		if (date != null && date.after(now)) {
			throw new ValidatorException(new FacesMessage(Messages.instance().get("validator.Date.PAST")));
		}
		
	}

}
