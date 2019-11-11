package br.com.infox.jsf.validator;

import java.math.BigDecimal;

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
		BigDecimal number = new BigDecimal(value.toString());
 		Object minAttr = component.getAttributes().get("min");
 		Object maxAttr = component.getAttributes().get("max");
		BigDecimal min = minAttr != null ? new BigDecimal(minAttr.toString()) : null;
		BigDecimal max = maxAttr != null ? new BigDecimal(maxAttr.toString()) : null;
		
		if (min != null && max != null && (number.compareTo(min) < 0 || number.compareTo(max) > 0)) {
			throw new ValidatorException(new FacesMessage("O valor deve estar entre " + min + " e " + max));
		}
		if (min != null && number.compareTo(min) < 0) {
			throw new ValidatorException(new FacesMessage("O valor deve ser maior ou igual a " + min));
		}
		if (max != null && number.compareTo(max) > 0) {
			throw new ValidatorException(new FacesMessage("O valor deve ser menor ou igual a " + max));
		}
	}
}
