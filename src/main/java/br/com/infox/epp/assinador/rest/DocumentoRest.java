package br.com.infox.epp.assinador.rest;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface DocumentoRest {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Documento> listar();
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response atualizarDocumentosLote(List<Documento> documentos);
	
	@Path("{uuid}")
	public DocumentoResource getDocumentoResource(@PathParam("uuid") UUID uuid);
	
}
