package br.com.infox.epp.assinador.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface AssinaturaRest {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response novaAssinatura(Assinatura assinatura);
	
}
