package br.com.infox.jsf.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("br.com.infox.PositiveIntegerValidator")
public class PositiveIntegerValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value == null) {
			return;
		}
		
		Integer number = (Integer) value;
		if (number < 0) {
			throw new ValidatorException(new FacesMessage("Somente números positivos"));
		}
	}
}
