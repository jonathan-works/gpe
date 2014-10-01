package br.com.infox.jsf.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@org.jboss.seam.annotations.faces.Validator(id = "emptyStringValidator")
@Name("emptyStringValidator")
@BypassInterceptors
public class EmptyStringValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, 
			Object value) throws ValidatorException {
		if (value == null || ((String) value).trim().length() == 0) {
			throw new ValidatorException(new FacesMessage("campo obrigatório"));
		}
	}

}
