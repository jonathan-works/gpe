package br.com.infox.epp.ws;

import javax.inject.Inject;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.ws.services.MensagensErroService;

@Provider
public class MapeadorExcecoesDao implements ExceptionMapper<DAOException> {

	@Inject
	private MensagensErroService mensagensErroService;
	
	@Inject
	private Logger logger;
	
	@Override
	public Response toResponse(DAOException exception) {
		String erroMsg = new Gson().toJson(mensagensErroService.getErro(exception));
		if (exception.getDatabaseErrorCode() != null) {
			return Response.status(Status.CONFLICT).entity(erroMsg).build();
		}
		return Response.status(Status.BAD_REQUEST).entity(erroMsg).build();
	}

}
