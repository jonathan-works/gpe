package br.com.infox.epp.entrega.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("item")
@Produces(MediaType.APPLICATION_JSON)
public class CategoriaEntregaItemRest {

	@Inject
	private CategoriaEntregaRestService categoriaEntregaRestService;
	
	@POST
	@Path("")
	public Response novoItem(Item item)
	{
		categoriaEntregaRestService.novoItem(item);
		return Response.ok().status(Status.CREATED).build();
	}
	
	@GET
	@Path("{idItem}/categoria")
	public List<Categoria> listarFilhas(@PathParam("idItem") Integer idItem)
	{
		return categoriaEntregaRestService.getCategoriasFilhas(idItem);
	}
	
}
