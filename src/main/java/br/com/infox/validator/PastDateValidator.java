package br.com.infox.validator;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@org.jboss.seam.annotations.faces.Validator(id="pastDateValidator")
@Name("pastDateValidator")
@BypassInterceptors
public class PastDateValidator implements Validator {

	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		Date date = (Date) value;
		Date now = new Date();
		if (date != null && date.after(now)) {
			throw new ValidatorException(new FacesMessage("A data deve ser menor ou IGUAL que a atual."));
		}
		
	}

}
