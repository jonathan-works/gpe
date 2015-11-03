package br.com.infox.epp.ws;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Lifecycle;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;

@Provider
public class MapeadorExcecoesDao implements ExceptionMapper<DAOException> {

	@Override
	public Response toResponse(DAOException exception) {
		Lifecycle.beginCall();
		String mensagem = null;
		try {
			ActionMessagesService actionMessagesService = (ActionMessagesService) Component
					.getInstance(ActionMessagesService.class);
			mensagem = actionMessagesService.handleDAOException(exception);
			if(mensagem == null)
			{
				mensagem = exception.getMessage();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(mensagem).build();
		} finally {
			Lifecycle.endCall();
		}
	}

}
