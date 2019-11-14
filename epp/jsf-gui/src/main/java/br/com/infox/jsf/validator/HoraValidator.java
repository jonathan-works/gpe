package br.com.infox.jsf.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("horaValidator")
public class HoraValidator implements Validator {

    private static final int MIN_HORA = 0;
    private static final int MAX_HORA = 23;
    private static final int MIN_MINUTO = 0;
    private static final int MAX_MINUTO = 59;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null || value.toString().trim().length() == 0) {
            return;
        }
        String[] horario = value.toString().split(":");
        Integer hora = null;
        Integer minuto = null;
        try {
            hora = Integer.parseInt(horario[0]);
            minuto = Integer.parseInt(horario[1]);
        } catch (NumberFormatException e) {
            throw new ValidatorException(new FacesMessage("Hora e/ou Minuto inválido"), e);
        }
        if (hora == null || hora > MAX_HORA || hora < MIN_HORA) {
            throw new ValidatorException(new FacesMessage("Selecionar hora entre 0 e 23"));
        }
        if (minuto == null || minuto > MAX_MINUTO || minuto < MIN_MINUTO) {
            throw new ValidatorException(new FacesMessage("Selecionar minuto entre 0 e 59"));
        }
    }

}
