package br.com.infox.epp.ws.interceptors;

import java.lang.reflect.Parameter;
import java.util.Set;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;

import br.com.infox.epp.ws.annotation.Validate;
import br.com.infox.epp.ws.messages.WSMessages;

/**
 * Interceptor responsável por validar parâmetros anotados com {@link Validate}
 * @author paulo
 *
 */
public class ValidacaoInterceptor {
	
	private static final String MSG_TEMPLATE_CODE = "ME_ATTR_%s_INVALIDO";
	
	private <T> void validar(T bean) throws ValidationException {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<T>> errors = validator.validate(bean);
		
		if (errors.size() != 0){
			for (ConstraintViolation<T> violation : errors) {
				String name = String.format(MSG_TEMPLATE_CODE, violation.getPropertyPath().toString().toUpperCase());
				throw new ValidationException(WSMessages.valueOf(name).codigo());
			}
		}
	}

	@AroundInvoke
	private Object validar(InvocationContext ctx) throws Exception {
		Parameter[] parametros = ctx.getMethod().getParameters();
		Object[] valoresParametros = ctx.getParameters();
		for(int i=0; i<parametros.length; i++)
		{
			if(parametros[i].getAnnotation(Validate.class) != null)
			{
				validar(valoresParametros[i]);
			}
		}
		return ctx.proceed();
	}
	

}
