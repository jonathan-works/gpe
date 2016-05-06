package br.com.infox.epp.entrega.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("categoria")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CategoriaEntregaRest {

	@Inject
	private CategoriaEntregaRestService categoriaEntregaRestService;
	
	@GET
	public List<Categoria> getCategoriasRoot()
	{
		return categoriaEntregaRestService.getCategoriasRoot();
	}
	
	@POST
	public Response novaCategoria(Categoria categoria)
	{
		categoriaEntregaRestService.novaCategoria(categoria);
		return Response.ok().status(Status.CREATED).build();
	}
}
