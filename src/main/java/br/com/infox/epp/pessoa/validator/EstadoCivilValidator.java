package br.com.infox.epp.pessoa.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.infox.epp.pessoa.annotation.EstadoCivil;
import br.com.infox.epp.pessoa.type.EstadoCivilEnum;

public class EstadoCivilValidator implements ConstraintValidator<EstadoCivil, String> {

	@Override
	public void initialize(EstadoCivil constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
	    if (value == null) return true;
		for (EstadoCivilEnum estadoCivilEnum : EstadoCivilEnum.values()){
			if (estadoCivilEnum.name().equals(value)){
				return true;
			}
		}
		return false;
	}

}
