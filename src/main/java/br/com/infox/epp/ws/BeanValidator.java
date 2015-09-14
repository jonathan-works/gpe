package br.com.infox.epp.ws;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;

import br.com.infox.epp.ws.messages.WSMessages;

public final class BeanValidator {
	
	private static final String MSG_TEMPLATE_CODE = "ME_ATTR_%s_INVALIDO";
	
	public static <T> void validate(T bean) throws ValidationException {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<T>> errors = validator.validate(bean);
		
		if (errors.size() != 0){
			for (ConstraintViolation<T> violation : errors) {
				String name = String.format(MSG_TEMPLATE_CODE, violation.getPropertyPath().toString().toUpperCase());
				throw new ValidationException(WSMessages.valueOf(name).codigo());
			}
		}
	}
}
