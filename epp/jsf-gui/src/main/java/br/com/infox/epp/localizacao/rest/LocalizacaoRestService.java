package br.com.infox.epp.localizacao.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.localizacao.EstruturaSearch;
import br.com.infox.epp.localizacao.LocalizacaoDTOSearch;
import br.com.infox.epp.localizacao.LocalizacaoSearch;
import br.com.infox.epp.ws.exception.ConflictWSException;
import br.com.infox.epp.ws.exception.NotFoundWSException;
import br.com.infox.epp.ws.interceptors.TokenAuthentication;
import br.com.infox.epp.ws.interceptors.ValidarParametros;

@Stateless
@TokenAuthentication
@ValidarParametros
public class LocalizacaoRestService {

	@Inject
	private LocalizacaoManager localizacaoManager;
	@Inject
	private LocalizacaoSearch localizacaoSearch;
	@Inject
	private LocalizacaoDTOSearch localizacaoDTOSearch;
	@Inject
	private EstruturaSearch estruturaSearch;
	
	public LocalizacaoDTO adicionarLocalizacao(LocalizacaoDTO localizacaoDTO) {
        Localizacao locExistente = localizacaoSearch.getLocalizacaoByCodigo(localizacaoDTO.getCodigo());
        if (locExistente != null) {
            throw new ConflictWSException("Já existe uma localização cadastrada com o código " + localizacaoDTO.getCodigo());
        }
        Localizacao localizacaoPai = localizacaoSearch.getLocalizacaoByCodigo(localizacaoDTO.getCodigoLocalizacaoSuperior());
        locExistente = localizacaoSearch.getLocalizacaoByLocalizacaoPaiAndDescricao(localizacaoPai, localizacaoDTO.getNome());
        if (locExistente != null) {
            throw new ConflictWSException(String.format("Já existe uma localização cadastrada com código de localização superior '%s' e descrição '%s'", localizacaoDTO.getCodigoLocalizacaoSuperior(), localizacaoDTO.getNome()));
        }

	    Localizacao localizacao = new Localizacao();
		localizacao.setCodigo(localizacaoDTO.getCodigo());
		localizacao.setLocalizacao(localizacaoDTO.getNome());
		localizacao.setLocalizacaoPai(localizacaoPai);
		if (localizacaoDTO.getCodigoEstrutura() != null){
			localizacao.setEstruturaFilho(estruturaSearch.getEstruturaByNome(localizacaoDTO.getCodigoEstrutura()));
		}
		localizacao.setAtivo(Boolean.TRUE);
		
		localizacaoManager.persist(localizacao);
		return new LocalizacaoDTO(localizacao);
	}

	public LocalizacaoDTO atualizarLocalizacao(String codigoLocalizacao, LocalizacaoDTO localizacaoDTO) {
		Localizacao localizacao = localizacaoSearch.getLocalizacaoByCodigo(codigoLocalizacao);
		if (localizacao ==  null) {
		    throw new NotFoundWSException("Não foi encontrada localização com código " + codigoLocalizacao);
		}
		localizacao.setLocalizacao(localizacaoDTO.getNome());
		localizacao.setLocalizacaoPai(localizacaoSearch.getLocalizacaoByCodigo(localizacaoDTO.getCodigoLocalizacaoSuperior()));
		if (localizacaoDTO.getCodigoEstrutura() != null){
			localizacao.setEstruturaFilho(estruturaSearch.getEstruturaByNome(localizacaoDTO.getCodigoEstrutura()));
		}
		return new LocalizacaoDTO(localizacaoManager.update(localizacao));
	}
	

	public void removerLocalizacao(String codigoLocalizacao) {
		Localizacao localizacao = localizacaoSearch.getLocalizacaoByCodigo(codigoLocalizacao);
		if (localizacao ==  null) {
            throw new NotFoundWSException("Não foi encontrada localização com código " + codigoLocalizacao);
        }
		localizacao.setAtivo(Boolean.FALSE);
		localizacaoManager.update(localizacao);
	}

	public LocalizacaoDTO getLocalizacao(String codigoLocalizacao) {
		Localizacao localizacao = localizacaoSearch.getLocalizacaoByCodigo(codigoLocalizacao);
		if (localizacao ==  null) {
            throw new NotFoundWSException("Não foi encontrada localização com código " + codigoLocalizacao);
        }
        return new LocalizacaoDTO(localizacao);
	}

	public List<LocalizacaoDTO> getLocalizacoes() {
		return localizacaoDTOSearch.getLocalizacaoDTOList();
	}

}
