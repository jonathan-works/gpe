package br.com.infox.validator;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@org.jboss.seam.annotations.faces.Validator(id="futureDateValidator")
@Name("futureDateValidator")
@BypassInterceptors
public class FutureDateValidator implements Validator {

	public void validate(FacesContext context, UIComponent component, Object value) {
		
		Date date = (Date) value;
		Date now = new Date();
		if (date != null && date.before(now)) {
			throw new ValidatorException(new FacesMessage("A data e a hora devem ser maior que a atual."));
		}
		
	}

}
