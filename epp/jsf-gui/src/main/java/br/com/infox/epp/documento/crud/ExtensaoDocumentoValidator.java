package br.com.infox.epp.documento.crud;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import br.com.infox.core.messages.InfoxMessages;

@FacesValidator("extensaoDocumentoValidator")
public class ExtensaoDocumentoValidator implements Validator {
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) {
		try {
			String extensao = (String) value;
			if (extensao.substring(0, 1).equals(".")) {
				String errorMessage = InfoxMessages.getInstance().get("javax.faces.validator.ExtensaoDocumentoValidator");
				throw new ValidatorException(new FacesMessage(errorMessage));
			}
		} catch (ValidatorException e) {
			throw new ValidatorException(new FacesMessage(e.getMessage()));
		}
	}

}
