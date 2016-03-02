package br.com.infox.epp.tipoParticipante.rest;

import static br.com.infox.epp.ws.RestUtils.produceErrorJson;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.infox.core.persistence.DAOException;
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
		try {
			tipoParteManager.persist(aplicar(tipoParticipanteDTO,new TipoParte()));
		} catch (DAOException e){
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(produceErrorJson(e.getMessage())).build());
		}
	}

	public void atualizarTipoParticipante(String codigo, TipoParticipanteDTO tipoParticipanteDTO) {
		try {
			tipoParteManager.update(aplicar(tipoParticipanteDTO,tipoParteSearch.getTipoParteByIdentificador(codigo)));
		} catch (DAOException e){
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(produceErrorJson(e.getMessage())).build());
		}
	}

	public void removerTipoParticipante(String codigo) {
		try {
			tipoParteManager.remove(tipoParteSearch.getTipoParteByIdentificador(codigo));
		} catch (DAOException e){
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(produceErrorJson(e.getMessage())).build());
		}
	}

	public TipoParte aplicar(TipoParticipanteDTO tipoParticipanteDTO, TipoParte tipoParte){
		tipoParte.setDescricao(tipoParticipanteDTO.getNome());
		tipoParte.setIdentificador(tipoParticipanteDTO.getCodigo());
		return tipoParte;
	}
	
}
