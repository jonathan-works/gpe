package br.com.infox.jsf.validator;

import javax.el.ELException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.el.parser.ELParser;

import br.com.infox.core.util.StringUtil;

@FacesValidator("javax.faces.elValidator")
public class ELValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (StringUtil.isEmpty((String) value)) return;
        try {
            ELParser.parse((String) value);
        } catch (ELException e) {
            throw new ValidatorException(new FacesMessage("Expressão inválida"), e);
        }
    }

}
