package br.com.infox.epp.localizacao.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/localizacao")
public interface LocalizacaoRest {
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Response adicionarLocalizacao(LocalizacaoDTO localizacao);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Response getLocalizacoes();
	
	@Path("/{codigo}")
	LocalizacaoResource getLocalizacaoResource(@PathParam("codigo") String codigo);

}
