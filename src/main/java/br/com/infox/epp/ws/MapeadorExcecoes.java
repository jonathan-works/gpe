package br.com.infox.epp.ws;

import static br.com.infox.epp.ws.services.MensagensErroService.CODIGO_ERRO_INDEFINIDO;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.infox.epp.rest.RestException;
import br.com.infox.epp.ws.exception.ExcecaoServico;
import br.com.infox.epp.ws.exception.ExcecaoServico.ErroServico;
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

	private int getStatus(Throwable e) {
		if(e instanceof EJBException && e.getCause() != null) {
			e = e.getCause();
		}
		if (e != null && ExcecaoServico.class.isAssignableFrom(e.getClass())) {
			return ((ExcecaoServico)e).getStatus();
		} else {
			return Status.INTERNAL_SERVER_ERROR.getStatusCode();
		}
	}

	@Override
	public Response toResponse(Throwable e) {
		if (e instanceof WebApplicationException) {
			WebApplicationException exception = (WebApplicationException) e;
			return exception.getResponse();
		}
		else if(e.getCause() instanceof WebApplicationException) {
			WebApplicationException exception = (WebApplicationException) e.getCause();
			return exception.getResponse();			
		}
		else if (e instanceof ValidationException) {
			WebApplicationException wae = buildWebApplicationException((ValidationException) e);
			return wae.getResponse();
		} else {
			ErroServico erro = mensagensService.getErro(e);
			String mensagemResposta = erro.getCodigo();
			if (CODIGO_ERRO_INDEFINIDO.equals(mensagemResposta)) {
				mensagemResposta = erro.getMensagem();
			}

			int status = getStatus(e);
			return Response.status(status).entity(mensagemResposta).build();
		}
	}

	private WebApplicationException buildWebApplicationException(ValidationException ve) {
		RestException re = new RestException(WSMessages.ME_ATRIBUTO_INVALIDO.codigo(), ve.getMessage());
		Gson gson = new GsonBuilder().create();
		Response response = Response.status(Status.BAD_REQUEST.getStatusCode()).entity(gson.toJson(re)).type(MediaType.APPLICATION_JSON).build();
		return new WebApplicationException(response);
	}
}
