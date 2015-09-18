package br.com.infox.epp.ws;

import javax.ejb.EJBException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import br.com.infox.epp.ws.exception.ExcecaoServico;
import br.com.infox.epp.ws.exception.ValidacaoException;

/**
 * Responsável por tratar as exceções de validação em serviços REST
 * 
 * @author paulo
 *
 */
@Provider
public class MapeadorExcecoesValidacao implements ExceptionMapper<Throwable> {

	private Status getStatus(ExcecaoServico ex) {
		if (ex instanceof ValidacaoException) {
			return Status.BAD_REQUEST;
		} else {
			return Status.INTERNAL_SERVER_ERROR;
		}
	}

	@Override
	public Response toResponse(Throwable e) {
		if(e instanceof EJBException && e.getCause() != null) {
			e = e.getCause();
		}
		if(ExcecaoServico.class.isAssignableFrom(e.getClass())) {
			ExcecaoServico ex = (ExcecaoServico) e;
			Status status = getStatus(ex);
				return Response.status(status).entity(ex.getErro().getCodigo()).build();						
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
	}
}
