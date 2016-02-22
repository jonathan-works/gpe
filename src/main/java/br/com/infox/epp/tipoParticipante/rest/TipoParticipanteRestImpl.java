package br.com.infox.epp.tipoParticipante.rest;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class TipoParticipanteRestImpl implements TipoParticipanteRest{

	@Inject
	private TipoParticipanteResource tipoParticipanteResource;
	@Inject
	private TipoParticipanteRestService tipoParticipanteRestService;
	
	@Override
	public Response adicionarTipoParticipante(TipoParticipanteDTO tipoParticipanteDTO) {
		tipoParticipanteRestService.adicionarTipoParticipante(tipoParticipanteDTO);
		return Response.ok().build();
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
