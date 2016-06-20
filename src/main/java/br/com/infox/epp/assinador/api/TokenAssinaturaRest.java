package br.com.infox.epp.assinador.api;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/tokenAssinatura")
public interface TokenAssinaturaRest {
	
	@Path("/{token}")
	public TokenAssinaturaResource getTokenAssinaturaResource(@PathParam("token") String token);

}
