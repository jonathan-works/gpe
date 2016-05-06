package br.com.infox.epp.entrega.rest;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
}
