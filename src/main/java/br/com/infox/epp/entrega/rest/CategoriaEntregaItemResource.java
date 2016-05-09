package br.com.infox.epp.entrega.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("item")
@Produces(MediaType.APPLICATION_JSON)
public interface CategoriaEntregaItemResource {

	@POST
	public Response novoItem(Item item);

	@Path("{id}/categoria")
	public CategoriaEntregaResource getCategoria(@PathParam("id") Integer id); 
	
	@Path("{id}/item")
	public CategoriaEntregaItemResource getItem(@PathParam("id") Integer id); 
}
