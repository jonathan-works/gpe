package br.com.infox.epp.meiocontato.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.infox.epp.meiocontato.annotation.Email;

public class EmailValidator implements ConstraintValidator<Email, String>{
	
	private String pattern;

	@Override
	public void initialize(Email emailAnnotation) {
		this.pattern = emailAnnotation.pattern();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null){
			return true;
		} else {
			return value.matches(pattern);
		}
	}

}
