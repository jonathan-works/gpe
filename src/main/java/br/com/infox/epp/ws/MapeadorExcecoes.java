package br.com.infox.epp.ws;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import br.com.infox.epp.rest.RestException;
import br.com.infox.epp.ws.exception.ExcecaoServico;
import br.com.infox.epp.ws.exception.MediaTypeSource;
import br.com.infox.epp.ws.messages.WSMessages;
import br.com.infox.epp.ws.services.MensagensErroService;

/**
 * Responsável por tratar as exceções de serviços REST
 * 
 * @author paulo
 *
 */
@Provider
public class MapeadorExcecoes implements ExceptionMapper<Throwable> {

	@Inject
	private MensagensErroService mensagensService;

	private Throwable getExcecao(Throwable e) {
		if (e instanceof EJBException && e.getCause() != null) {
			return e.getCause();
		}
		return e;
	}

	private int getStatus(Throwable e) {
		if (e != null && ExcecaoServico.class.isAssignableFrom(e.getClass())) {
			return ((ExcecaoServico) e).getStatus();
		} else {
			return Status.INTERNAL_SERVER_ERROR.getStatusCode();
		}
	}

	@Override
	public Response toResponse(Throwable e) {
		e = getExcecao(e);
		if(e instanceof ExcecaoServico) {
			WebApplicationException wae = buildWebApplicationException((ExcecaoServico) e);
			
			return wae.getResponse();
		}
		else if (e instanceof WebApplicationException) {
			WebApplicationException exception = (WebApplicationException) e;
			return exception.getResponse();
		} else if (e instanceof NoResultException) {
			WebApplicationException wae = buildWebApplicationException((NoResultException) e);
			return wae.getResponse();
		} else {
			int status = getStatus(e);
			return Response.status(status).entity(mensagensService.getErro(e)).type(MediaType.APPLICATION_JSON).build();
		}
	}
	
	private void setMediaType(ResponseBuilder responseBuilder, Object e) {
		if(e instanceof MediaTypeSource) {
			String mediaType = ((MediaTypeSource)e).getMediatType();
			responseBuilder.type(mediaType);
		}
	}

	private WebApplicationException buildWebApplicationException(ExcecaoServico excecaoServico) {
		RestException re = new RestException(excecaoServico.getCode(), excecaoServico.getMessage());
		ResponseBuilder response = Response.status(excecaoServico.getStatus()).entity(re);
		setMediaType(response, excecaoServico);
		return new WebApplicationException(response.build());
	}
	
	private WebApplicationException buildWebApplicationException(NoResultException e) {
		RestException re = new RestException(WSMessages.ME_OBJETO_NAO_ENCONTRADO.codigo(), WSMessages.ME_OBJETO_NAO_ENCONTRADO.label());
		ResponseBuilder response = Response.status(Status.NOT_FOUND.getStatusCode()).entity(re);
		setMediaType(response, e);
		return new WebApplicationException(response.build());
	}
}
