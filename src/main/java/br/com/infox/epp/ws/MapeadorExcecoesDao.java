package br.com.infox.epp.ws;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.jboss.seam.Component;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;

public class MapeadorExcecoesDao implements ExceptionMapper<DAOException> {

	@Override
	public Response toResponse(DAOException exception) {
		ActionMessagesService actionMessagesService = (ActionMessagesService) Component.getInstance(ActionMessagesService.class); 
		
		String mensagem = actionMessagesService.handleDAOException(exception);
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(mensagem).build();
	}

}
