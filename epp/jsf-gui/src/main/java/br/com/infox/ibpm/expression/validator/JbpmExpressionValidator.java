package br.com.infox.ibpm.expression.validator;

import javax.el.ELException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.el.parser.ELParser;

@FacesValidator("jbpmExpressionValidator")
public class JbpmExpressionValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value != null) {
			try {
				ELParser.parse((String) value);
			} catch (ELException e) {
				e.printStackTrace();
				throw new ValidatorException(new FacesMessage("Erro na expressão"), e);
			}
		}
	}
}
