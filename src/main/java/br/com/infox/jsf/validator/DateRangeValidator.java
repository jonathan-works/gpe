package br.com.infox.jsf.validator;

import java.util.Date;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.util.DateUtil;

@FacesValidator("dateRangeValidator")
public class DateRangeValidator implements Validator {
	
    public void validate(FacesContext context, UIComponent component, Object value) {
        Date startDate = null, endDate = null;
        String errorMessage = null;
        
        Map<String, Object> attr = component.getAttributes();
        startDate = (Date) attr.get("startDate");
        endDate = (Date) attr.get("endDate");
        errorMessage = (String) attr.get("errorMessage");
        if (errorMessage == null) {
            errorMessage = InfoxMessages.getInstance().get("validator.Date.Range");
        }
        
        if (value == null) {
            return;
        }
        Date date = (Date) value;
        if (endDate == null && startDate == null) {
            return;
        }
        if (endDate != null) {
            endDate = DateUtil.getEndOfDay(endDate);
            if (date != null && date.after(endDate)) {
                throw new ValidatorException(new FacesMessage(errorMessage));
            }
        }
        if (startDate != null) {
            startDate = DateUtil.getBeginningOfDay(startDate);
            if (date != null && date.before(startDate)) {
                throw new ValidatorException(new FacesMessage(errorMessage));
            }
        }
    }

}