package br.com.infox.epp.assinador.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface AssinaturaRest {

	@Path("/")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response novaAssinatura(Assinatura assinatura);
	
	@Path("/lote")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response assinarDocumentosLote(List<Assinatura> assinaturas);
}
