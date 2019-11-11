package br.com.infox.jsf.validator;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import br.com.infox.core.messages.InfoxMessages;

@FacesValidator("pastDateValidator")
public class PastDateValidator implements Validator {

    public void validate(FacesContext context, UIComponent component, Object value) {

        Date date = (Date) value;
        Date now = new Date();
        if (date != null && date.after(now)) {
            throw new ValidatorException(new FacesMessage(InfoxMessages.getInstance().get("validator.Date.PAST")));
        }

    }

}
