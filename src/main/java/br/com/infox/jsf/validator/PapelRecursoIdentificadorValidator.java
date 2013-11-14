package br.com.infox.jsf.validator;

import java.text.MessageFormat;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(value = PapelRecursoIdentificadorValidator.VALIDATOR_ID)
public class PapelRecursoIdentificadorValidator implements Validator {

	public static final String VALIDATOR_ID = "papelRecursoIdentificadorValidator";
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value == null) {
			return;
		}
		
		boolean isPapel = Boolean.parseBoolean((String) component.getAttributes().get("isPapel"));
		String identificador = (String) value;
		String mensagem = MessageFormat.format("O {0} {1}deve iniciar com /", 
				isPapel ? "papel" : "recurso",
				isPapel ? "não " : "");
		if ((isPapel && identificador.startsWith("/")) || (!isPapel && !identificador.startsWith("/"))) {
			throw new ValidatorException(new FacesMessage(mensagem));
		}
	}
}
