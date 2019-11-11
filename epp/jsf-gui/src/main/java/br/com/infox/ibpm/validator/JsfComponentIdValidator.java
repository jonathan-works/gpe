package br.com.infox.ibpm.validator;

import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("jsfComponentIdValidator")
public class JsfComponentIdValidator implements Validator {

    public void validate(FacesContext fc, UIComponent ui, Object obj) {
        String id = (String) obj;
        UIComponent test = new UIMessage();
        try {
            test.setId(id);
            if (!Pattern.compile("^[a-z|A-Z][a-z|A-Z|0-9|\\-|_]*$").matcher(id).find()) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            throw new ValidatorException(new FacesMessage(
                    "Identificador inválido. Deve iniciar com uma letra, e deve conter apenas letras, números, hífens ou underscores."), e);
        }
    }

}
