package br.com.infox.epp.tipoParticipante.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/tipoParticipante")
public interface TipoParticipanteRest {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Response adicionarTipoParticipante(@Context UriInfo uriInfo, TipoParticipanteDTO tipoParticipante);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Response getTiposParticipante();

	@Path("/{codigo}")
	TipoParticipanteResource getTipoParticipanteResource(@PathParam("codigo") String codigo);

}
