package br.com.infox.epp.entrega.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("categoria")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CategoriaEntregaResource {

	@GET
	public List<Categoria> getCategorias();
	
	@POST
	public Response novaCategoria(Categoria categoria);
	
	@Path("{codigo}/item")
	public CategoriaEntregaItemResource getItem(@PathParam("codigo") String codigo); 



}
