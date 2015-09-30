package br.com.infox.jsf.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.faces.FacesMessages;

@FacesValidator("porcentagemValidator")
public class PorcentagemValidator implements Validator{

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if(value != null){
			double valor = (Double)value;
			if(valor < 0 || valor > 1){
				String globalValidationMessage = (String) component.getAttributes().get("globalValidationMessage");
				if(globalValidationMessage != null && globalValidationMessage.equals("true")){
					FacesMessages.instance().add("Valor do tipo porcentagem deve estar entre 0 e 100.");
				}
				throw new ValidatorException(new FacesMessage("Valor do tipo porcentagem deve estar entre 0 e 100."));
			}
		}
	}

}
