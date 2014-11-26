package br.com.infox.epp.documento.crud;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.messages.Messages;

@org.jboss.seam.annotations.faces.Validator(id = ExtensaoDocumentoValidator.NAME)
@Name(ExtensaoDocumentoValidator.NAME)
@BypassInterceptors
public class ExtensaoDocumentoValidator implements Validator{
	public static final String NAME = "extensaoDocumentoValidator";

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) {
		try {
			String extensao = (String) value;
			if (extensao.substring(0, 1).equals(".")) {
				String errorMessage = Messages.resolveMessage("javax.faces.validator.ExtensaoDocumentoValidator");
				throw new ValidatorException(new FacesMessage(errorMessage));
			}
		} catch (ValidatorException e) {
			throw new ValidatorException(new FacesMessage(e.getMessage()));
		}
	}

}
