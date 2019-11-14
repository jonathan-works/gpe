package br.com.infox.epp.tipoParticipante.rest;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public class TipoParticipanteRestImpl implements TipoParticipanteRest{

	@Inject
	private TipoParticipanteResource tipoParticipanteResource;
	@Inject
	private TipoParticipanteRestService tipoParticipanteRestService;
	
	@Override
	public Response adicionarTipoParticipante(UriInfo uriInfo, TipoParticipanteDTO tipoParticipanteDTO) {
		tipoParticipanteRestService.adicionarTipoParticipante(tipoParticipanteDTO);
		URI location = uriInfo.getAbsolutePathBuilder().path(tipoParticipanteDTO.getCodigo()).build();
        return Response.created(location).build();
	}

	@Override
	public Response getTiposParticipante() {
		return Response.ok(tipoParticipanteRestService.getTiposParticipantes()).build();
	}

	@Override
	public TipoParticipanteResource getTipoParticipanteResource(String codigo) {
		tipoParticipanteResource.setCodigo(codigo);
		return tipoParticipanteResource;
	}

}
