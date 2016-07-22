package br.com.infox.epp.ws;

import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import br.com.infox.epp.rest.RestException;
import br.com.infox.epp.ws.exception.ErroServico;
import br.com.infox.epp.ws.exception.MediaTypeSource;
import br.com.infox.epp.ws.exception.ValidacaoException;
import br.com.infox.epp.ws.services.MensagensErroService;

public class MapeadorExcecoesValidation implements ExceptionMapper<ValidationException>{

	@Override
	public Response toResponse(ValidationException exception) {
		WebApplicationException wae = buildWebApplicationException((ValidationException) exception);
		return wae.getResponse();
	}
	
	private void setMediaType(ResponseBuilder responseBuilder, Object e) {
		if(e instanceof MediaTypeSource) {
			String mediaType = ((MediaTypeSource)e).getMediatType();
			responseBuilder.type(mediaType);
		}
	}	

	private WebApplicationException buildWebApplicationException(ValidationException ve) {
		RestException re = null;
		if (ve instanceof ErroServico) {
			ErroServico erroServico = (ValidacaoException) ve;
			re = new RestException(erroServico.getCode(), erroServico.getMessage());
		} else {
			re = new RestException(MensagensErroService.CODIGO_VALIDACAO, ve.getMessage());
		}
		ResponseBuilder response = Response.status(Status.BAD_REQUEST.getStatusCode()).entity(re);
		setMediaType(response, ve);
		return new WebApplicationException(response.build());
	}
	
}
