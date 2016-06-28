package br.com.infox.epp.assinador.api;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

public interface TokenAssinaturaResource {
	
	@Path("cancelado")
	@POST
	public void processamentoCancelado();
	
	@GET
	@Path("status")
	public String getStatus();
	
	@Path("assinavel")
	public AssinavelResource getAssinavelResource();
}
