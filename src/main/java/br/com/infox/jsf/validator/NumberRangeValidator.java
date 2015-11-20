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
		String minAttr = (String) component.getAttributes().get("min");
		String maxAttr = (String) component.getAttributes().get("max");
		Integer min = minAttr != null ? Integer.parseInt(minAttr) : null;
		Integer max = maxAttr != null ? Integer.parseInt(maxAttr) : null;
		if (min != null && max != null && (number < min.intValue() || number > max.intValue())) {
			throw new ValidatorException(new FacesMessage("O valor deve estar entre " + min + " e " + max));
		}
		if (min != null && number < min.intValue()) {
			throw new ValidatorException(new FacesMessage("O valor deve ser maior ou igual a " + min));
		}
		if (max != null && number > max.intValue()) {
			throw new ValidatorException(new FacesMessage("O valor deve ser menor ou igual a " + max));
		}
	}
}
