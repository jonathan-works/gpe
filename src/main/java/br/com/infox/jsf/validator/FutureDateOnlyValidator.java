package br.com.infox.jsf.validator;

import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import br.com.infox.core.messages.InfoxMessages;

@FacesValidator("futureDateOnlyValidator")
public class FutureDateOnlyValidator implements Validator {

    public void validate(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return;
        }
        Calendar data = Calendar.getInstance();
        data.setTime((Date) value);
        Calendar dataAtual = Calendar.getInstance();
        dataAtual.set(Calendar.HOUR_OF_DAY, 23);
        dataAtual.set(Calendar.MINUTE, 59);
        dataAtual.set(Calendar.SECOND, 59);
        dataAtual.set(Calendar.MILLISECOND, 999);
        if (data.equals(dataAtual) || data.before(dataAtual)) {
            throw new ValidatorException(new FacesMessage(InfoxMessages.getInstance().get("validator.Date.FUTURE_ONLY")));
        }
    }
}
