package br.com.infox.epp.tipoParticipante.rest;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import br.com.infox.epp.ws.interceptors.Log;

@Log
public class TipoParticipanteResourceImpl implements TipoParticipanteResource{

	@Inject
	private TipoParticipanteRestService tipoParticipanteRestService;
	private String codigo;
	
	@Override
	public Response getTipoParticipante() {
		return Response.ok(tipoParticipanteRestService.getTipoParticipanteByCodigo(codigo)).build();
	}

	@Override
	public Response atualizarTipoParticipante(TipoParticipanteDTO tipoParticipanteDTO) {
		tipoParticipanteRestService.atualizarTipoParticipante(codigo, tipoParticipanteDTO);
		return Response.ok().build();
	}

	@Override
	public Response removerTipoParticipante() {
		tipoParticipanteRestService.removerTipoParticipante(codigo);
		return Response.ok().build();
	}

	@Override
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

}
