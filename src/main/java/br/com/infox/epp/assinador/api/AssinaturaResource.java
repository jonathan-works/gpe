package br.com.infox.epp.assinador.api;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

public interface AssinaturaResource {
	
	@POST
	public Response assinar(Assinatura assinatura);
}
