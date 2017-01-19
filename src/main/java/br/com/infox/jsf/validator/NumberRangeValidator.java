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
		double number = ((double) value);
 		Object minAttr = component.getAttributes().get("min");
 		Object maxAttr = component.getAttributes().get("max");
		Double min = minAttr != null ? Double.parseDouble(minAttr.toString()) : null;
		Double max = maxAttr != null ? Double.parseDouble(maxAttr.toString()) : null;
		if (min != null && max != null && (number < min || number > max)) {
			throw new ValidatorException(new FacesMessage("O valor deve estar entre " + min + " e " + max));
		}
		if (min != null && number < min) {
			throw new ValidatorException(new FacesMessage("O valor deve ser maior ou igual a " + min));
		}
		if (max != null && number > max) {
			throw new ValidatorException(new FacesMessage("O valor deve ser menor ou igual a " + max));
		}
	}
}
