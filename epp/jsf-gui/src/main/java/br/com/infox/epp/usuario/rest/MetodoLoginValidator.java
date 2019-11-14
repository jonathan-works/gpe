package br.com.infox.epp.usuario.rest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MetodoLoginValidator implements ConstraintValidator<MetodoLogin, String> {

    @Override
    public void initialize(MetodoLogin constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        switch (value) {
        case "CERTIFICADO":
        case "SENHA":
        case "SEM_LOGIN":
        case "SENHA_E_CERTIFICADO":
            return true;
        default:
            return false;
        }
    }

}
