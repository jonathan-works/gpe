package br.com.infox.ibpm.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(value = ItensComboFluxoValidator.VALIDATOR_ID)
public class ItensComboFluxoValidator implements Validator {

    public static final String VALIDATOR_ID = "itensComboFluxoValidator";

    /**
     * @throws ValidatorException
     * */
    @Override
    public void validate(FacesContext context, UIComponent component,
            Object value) {
        if (value != null && ((String) value).contains(":")) {
            throw new ValidatorException(new FacesMessage("Não pode conter :"));
        }
    }
}
