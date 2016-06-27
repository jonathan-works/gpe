package br.com.infox.epp.assinador.api;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public interface AssinavelResource {

	@Path("/")
	@GET
	public List<Assinavel> listarAssinaveis();
	
	@Path("{uuid}/sha256")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getSHA256Hex(@PathParam("uuid") UUID uuid);
	
	@Path("{uuid}/sha256")
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public byte[] getSHA256(@PathParam("uuid") UUID uuid);
	
	@Path("{uuid}/assinatura")
	public AssinaturaResource getAssinaturaResource(@PathParam("uuid") UUID uuid);

}
