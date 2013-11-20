package br.com.infox.validator;

import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.international.Messages;

@org.jboss.seam.annotations.faces.Validator(id=FutureDateValidator.NAME)
@Name(FutureDateValidator.NAME)
@BypassInterceptors
public class FutureDateValidator implements Validator {

	static final String NAME = "futureDateValidator";

    public void validate(FacesContext context, UIComponent component, Object value) {
    	if (value == null) {
    		return;
    	}
    	
    	Calendar data = Calendar.getInstance();
    	data.setTime((Date) value);
    	
    	Calendar dataAtual = Calendar.getInstance();
    	dataAtual.set(Calendar.HOUR, 0);
    	dataAtual.set(Calendar.MINUTE, 0);
    	dataAtual.set(Calendar.SECOND, 0);
    	dataAtual.set(Calendar.MILLISECOND, 0);
    	if (data.equals(dataAtual) || data.before(dataAtual)) {
			throw new ValidatorException(new FacesMessage(Messages.instance().get("validator.Date.FUTURE")));
		}
	}
}
