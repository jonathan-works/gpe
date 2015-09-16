package br.com.infox.epp.ws.interceptors;

import java.util.Set;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;

import br.com.infox.epp.ws.messages.WSMessages;

/**
 * Interceptor responsável por validar parâmetros anotados com
 * {@link ValidarParametros}
 * 
 * @author paulo
 *
 */
@ValidarParametros
@Interceptor
public class ValidacaoInterceptor {

	private static final String MSG_TEMPLATE_CODE = "ME_ATTR_%s_INVALIDO";

	private <T> void validar(T bean) throws ValidationException {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<T>> errors = validator.validate(bean);

		if (errors.size() != 0) {
			for (ConstraintViolation<T> violation : errors) {
				String name = String.format(MSG_TEMPLATE_CODE, violation.getPropertyPath().toString().toUpperCase());
				throw new ValidationException(WSMessages.valueOf(name).codigo());
			}
		}
	}

	@AroundInvoke
	private Object validar(InvocationContext ctx) throws Exception {
		for (Object valor : ctx.getParameters()) {
			validar(valor);
		}
		return ctx.proceed();
	}

}
