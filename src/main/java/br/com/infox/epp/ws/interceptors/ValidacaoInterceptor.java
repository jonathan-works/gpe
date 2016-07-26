package br.com.infox.epp.ws.interceptors;

import static java.text.MessageFormat.format;

import java.util.Set;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;

import br.com.infox.epp.ws.exception.ValidacaoException;
import br.com.infox.epp.ws.services.MensagensErroService;

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

	private <T> void validar(T bean) throws ValidationException {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<T>> errors = validator.validate(bean);

		if(errors.size() > 0) {
			ValidacaoException excecao = new ValidacaoException();
			for (ConstraintViolation<T> violation : errors) {
				excecao.adicionar(MensagensErroService.CODIGO_VALIDACAO, format("{0}: {1}; ", violation.getPropertyPath(), violation.getMessage()));
			}
			throw excecao;			
		}
	}

	@AroundInvoke
	private Object validar(InvocationContext ctx) throws Exception {
		if(ctx.getParameters() != null) {
			for (Object valor : ctx.getParameters()) {
				if(valor != null) {
					validar(valor);					
				}
			}	
		}
		return ctx.proceed();
	}

}
