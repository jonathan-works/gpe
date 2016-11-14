package br.com.infox.epp.tipoParticipante.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.manager.TipoParteManager;
import br.com.infox.epp.tipoParte.TipoParteSearch;
import br.com.infox.epp.tipoParticipante.TipoParticipanteDTOSearch;
import br.com.infox.epp.ws.exception.ConflictWSException;
import br.com.infox.epp.ws.exception.NotFoundWSException;
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
		TipoParte tipoParte = tipoParteSearch.getTipoParteByIdentificador(codigo);
		if (tipoParte ==  null) {
            throw new NotFoundWSException("Não foi encontrado tipo de parte com código " + codigo);
        }
        return new TipoParticipanteDTO(tipoParte);
	}

	public List<TipoParticipanteDTO> getTiposParticipantes() {
		return tipoParticipanteDTOSearch.getTipoParticipanteDTOList();
	}

	public void adicionarTipoParticipante(TipoParticipanteDTO tipoParticipanteDTO) {
	    if (tipoParteSearch.getTipoParteByIdentificador(tipoParticipanteDTO.getCodigo()) != null) {
	        throw new ConflictWSException("Já existe um tipo de participante com o código " + tipoParticipanteDTO.getCodigo());
	    }
		tipoParteManager.persist(aplicar(tipoParticipanteDTO,new TipoParte()));
	}

	public void atualizarTipoParticipante(String codigo, TipoParticipanteDTO tipoParticipanteDTO) {
		TipoParte tipoParte = tipoParteSearch.getTipoParteByIdentificador(codigo);
		if (tipoParte ==  null) {
            throw new NotFoundWSException("Não foi encontrado tipo de parte com código " + codigo);
        }
        tipoParteManager.update(aplicar(tipoParticipanteDTO,tipoParte));
	}

	public void removerTipoParticipante(String codigo) {
		TipoParte tipoParte = tipoParteSearch.getTipoParteByIdentificador(codigo);
		if (tipoParte ==  null) {
            throw new NotFoundWSException("Não foi encontrado tipo de parte com código " + codigo);
        }
        tipoParteManager.remove(tipoParte);
	}

	public TipoParte aplicar(TipoParticipanteDTO tipoParticipanteDTO, TipoParte tipoParte){
		tipoParte.setDescricao(tipoParticipanteDTO.getNome());
		tipoParte.setIdentificador(tipoParticipanteDTO.getCodigo());
		return tipoParte;
	}
	
}
