package br.com.infox.epp.assinador.api;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.infox.epp.rest.RestException;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AssinavelResource {

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
	
	@POST
	@Path("{uuid}/erro")
	public Response erro(@PathParam("uuid") UUID uuid, RestException erro);
	

}
