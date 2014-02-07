package br.com.infox.ibpm.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@org.jboss.seam.annotations.faces.Validator
@Name("jsfComponentIdValidator")
@BypassInterceptors
public class JsfComponentIdValidator implements Validator {

    public void validate(FacesContext fc, UIComponent ui, Object obj) {
        String id = (String) obj;
        UIComponent test = new UIMessage();
        try {
            test.setId(id);
        } catch (IllegalArgumentException e) {
            throw new ValidatorException(new FacesMessage("Identificador inválido. Deve iniciar com uma letra, e deve conter apenas letras, números, hífens ou underscores."), e);
        }
    }

}
