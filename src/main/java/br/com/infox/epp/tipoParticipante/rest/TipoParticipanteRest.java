package br.com.infox.epp.tipoParticipante.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/tipoParticipante")
public interface TipoParticipanteRest {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Response adicionarTipoParticipante(TipoParticipanteDTO tipoParticipante);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Response getTiposParticipante();

	@Path("/{codigo}")
	TipoParticipanteResource getTipoParticipanteResource(@PathParam("codigo") String codigo);

}
