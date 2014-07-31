package br.com.infox.jsf.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@org.jboss.seam.annotations.faces.Validator(id = CpfValidator.NAME)
@Name(CpfValidator.NAME)
@BypassInterceptors
public class CpfValidator implements Validator {
    public static final String NAME = "cpfValidator";

    public void validate(FacesContext context, UIComponent component,
            Object value) {
        try {
            String cpfValue = (String) value;
            cpfValue = cpfValue.replaceAll("\\.", "");
            cpfValue = cpfValue.replaceAll("-", "");
            cpfValue = cpfValue.replaceAll("_", "");
            Pattern p = Pattern.compile("^[0-9]{11}$");
            Matcher m = p.matcher(cpfValue);
            if (!m.matches() || !validarCpf(cpfValue)) {
                throw new ValidatorException(new FacesMessage("CPF inválido"));
            }
        } catch (Exception e) {
            throw new ValidatorException(new FacesMessage("CPF inválido"), e);
        }
    }

    public boolean validarCpf(String cpf) {
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma = soma + parseInt(cpf.charAt(i)) * (10 - i);
        }
        int dv1 = 11 - (soma % 11);
        if (dv1 == 10 || dv1 == 11) {
            dv1 = 0;
        }
        if (dv1 != parseInt(cpf.charAt(9))) {
            return false;
        }
        soma = 0;
        for (int i = 0; i < 9; i++) {
            soma = soma + parseInt(cpf.charAt(i)) * (11 - i);
        }
        soma = soma + dv1 * 2;
        int dv2 = 11 - (soma % 11);
        if (dv2 == 10 || dv2 == 11) {
            dv2 = 0;
        }
        if (dv2 != parseInt(cpf.charAt(10))) {
            return false;
        }
        return true;
    }

    private int parseInt(Character c) {
        return Integer.parseInt(c.toString());
    }

}
