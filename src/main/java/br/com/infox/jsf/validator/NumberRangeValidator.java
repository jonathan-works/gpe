package br.com.infox.jsf.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("numberRangeValidator")
public class NumberRangeValidator implements Validator {
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value == null) {
			return;
		}
		int number = ((Integer) value).intValue();
		Integer min = Integer.parseInt((String) component.getAttributes().get("min"));
		Integer max =  Integer.parseInt((String) component.getAttributes().get("max"));
		if (min != null && max != null && (number < min.intValue() || number > max.intValue())) {
			throw new ValidatorException(new FacesMessage("O valor deve estar entre " + min + " e " + max));
		}
		if (min != null && number < min.intValue()) {
			throw new ValidatorException(new FacesMessage("O valor deve ser maior que " + min));
		}
		if (max != null && number > max.intValue()) {
			throw new ValidatorException(new FacesMessage("O valor deve ser menor que " + max));
		}
	}
}
