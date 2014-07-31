package br.com.infox.epp.meiocontato.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.infox.epp.meiocontato.annotation.Telefone;

public class TelefoneValidator implements ConstraintValidator<Telefone, String>{
	
	private String pattern;

	@Override
	public void initialize(Telefone telefone) {
		this.pattern = telefone.pattern();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		} else {
			return value.matches(this.pattern);
		}
	}

}
