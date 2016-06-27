package br.com.infox.epp.assinador.api;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import br.com.infox.epp.rest.RestException;

public interface AssinaturaResource {
	
	@POST
	public Response assinar(Assinatura assinatura);

	@POST
	@Path("erro")
	public Response erro(RestException erro);
}
