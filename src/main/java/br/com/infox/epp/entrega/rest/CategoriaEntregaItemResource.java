package br.com.infox.epp.entrega.rest;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("item")
@Produces(MediaType.APPLICATION_JSON)
public interface CategoriaEntregaItemResource {

	@GET
	public List<Item> list();
	
	@GET
	@Path("{codigo}")
	public Item get(@PathParam("codigo") String codigo);
	
	@DELETE
	@Path("{codigo}")
	public void remove(@PathParam("codigo") String codigo);
	
	@POST
	public Response novoItem(Item item);

	@Path("{codigo}/categoria")
	public CategoriaEntregaResource getCategoria(@PathParam("codigo") String codigoItemPai); 
	
	@Path("{codigo}/item")
	public CategoriaEntregaItemResource getItem(@PathParam("codigo") String codigoItemPai); 
}
