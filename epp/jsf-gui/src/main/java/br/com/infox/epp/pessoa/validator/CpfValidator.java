package br.com.infox.epp.pessoa.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.infox.epp.pessoa.annotation.Cpf;

public class CpfValidator implements ConstraintValidator<Cpf, String> {
	
	private String pattern;

	@Override
	public void initialize(Cpf cpf) {
		this.pattern = cpf.pattern();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null){
			return true;
		} else {
			br.com.infox.jsf.validator.CpfValidator cpfValidator = new br.com.infox.jsf.validator.CpfValidator();
			return value.matches(pattern) && cpfValidator.validarCpf(value);
		}
	}

}
