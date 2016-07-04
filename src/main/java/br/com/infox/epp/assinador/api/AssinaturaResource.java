package br.com.infox.epp.assinador.api;

import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
public interface AssinaturaResource {
	
	@POST
	public Response assinar(Assinatura assinatura);
}
