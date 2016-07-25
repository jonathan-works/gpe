package br.com.infox.epp.ws;

import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.infox.epp.rest.RestException;
import br.com.infox.epp.ws.exception.ValidacaoException;
import br.com.infox.epp.ws.services.MensagensErroService;

public class MapeadorExcecoesValidation implements ExceptionMapper<ValidationException>{

	@Override
	public Response toResponse(ValidationException exception) {
		WebApplicationException wae = buildWebApplicationException((ValidationException) exception);
		return wae.getResponse();
	}

	private WebApplicationException buildWebApplicationException(ValidationException ve) {
		String jsonErro;
		Gson gson = new GsonBuilder().create();
		if (ve instanceof ValidacaoException) {
			ValidacaoException exception = (ValidacaoException) ve;
			jsonErro = gson.toJson(exception.getErro());
		} else {
			RestException re = new RestException(MensagensErroService.CODIGO_VALIDACAO, ve.getMessage());
			jsonErro = gson.toJson(re);
		}
		Response response = Response.status(Status.BAD_REQUEST.getStatusCode()).entity(jsonErro).type(MediaType.APPLICATION_JSON).build();
		return new WebApplicationException(response);
	}
	
}
