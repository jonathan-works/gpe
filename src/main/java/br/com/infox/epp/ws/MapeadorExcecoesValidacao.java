package br.com.infox.epp.ws;

import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Responsável por tratar as exceções de validação em serviços REST 
 * @author paulo
 *
 */
@Provider
public class MapeadorExcecoesValidacao implements ExceptionMapper<ValidationException> {

	@Override
	public Response toResponse(ValidationException exception) {
		return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
	}

}
