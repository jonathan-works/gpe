package br.com.infox.epp.tipoParticipante.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.manager.TipoParteManager;
import br.com.infox.epp.tipoParte.TipoParteSearch;
import br.com.infox.epp.tipoParticipante.TipoParticipanteDTOSearch;
import br.com.infox.epp.ws.interceptors.TokenAuthentication;
import br.com.infox.epp.ws.interceptors.ValidarParametros;

@Stateless
@TokenAuthentication
@ValidarParametros
public class TipoParticipanteRestService {

	@Inject
	private TipoParteSearch tipoParteSearch;
	@Inject
	private TipoParticipanteDTOSearch tipoParticipanteDTOSearch;
	@Inject
	private TipoParteManager tipoParteManager;

	public TipoParticipanteDTO getTipoParticipanteByCodigo(String codigo) {
		return new TipoParticipanteDTO(tipoParteSearch.getTipoParteByIdentificador(codigo));
	}

	public List<TipoParticipanteDTO> getTiposParticipantes() {
		return tipoParticipanteDTOSearch.getTipoParticipanteDTOList();
	}

	public void adicionarTipoParticipante(TipoParticipanteDTO tipoParticipanteDTO) {
		tipoParteManager.persist(aplicar(tipoParticipanteDTO,new TipoParte()));
	}

	public void atualizarTipoParticipante(String codigo, TipoParticipanteDTO tipoParticipanteDTO) {
		tipoParteManager.update(aplicar(tipoParticipanteDTO,tipoParteSearch.getTipoParteByIdentificador(codigo)));
	}

	public void removerTipoParticipante(String codigo) {
		tipoParteManager.remove(tipoParteSearch.getTipoParteByIdentificador(codigo));
	}

	public TipoParte aplicar(TipoParticipanteDTO tipoParticipanteDTO, TipoParte tipoParte){
		tipoParte.setDescricao(tipoParticipanteDTO.getNome());
		tipoParte.setIdentificador(tipoParticipanteDTO.getCodigo());
		return tipoParte;
	}
	
}
