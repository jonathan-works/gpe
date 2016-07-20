package br.com.infox.epp.localizacao.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.localizacao.EstruturaSearch;
import br.com.infox.epp.localizacao.LocalizacaoDTOSearch;
import br.com.infox.epp.localizacao.LocalizacaoSearch;
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
		Localizacao localizacao = new Localizacao();
		localizacao.setCodigo(localizacaoDTO.getCodigo());
		localizacao.setLocalizacao(localizacaoDTO.getNome());
		localizacao.setLocalizacaoPai(localizacaoSearch.getLocalizacaoByCodigo(localizacaoDTO.getCodigoLocalizacaoSuperior()));
		if (localizacaoDTO.getCodigoEstrutura() != null){
			localizacao.setEstruturaFilho(estruturaSearch.getEstruturaByNome(localizacaoDTO.getCodigoEstrutura()));
		}
		localizacao.setAtivo(Boolean.TRUE);
		
		localizacaoManager.persist(localizacao);
		return new LocalizacaoDTO(localizacao);
	}

	public LocalizacaoDTO atualizarLocalizacao(String codigoLocalizacao, LocalizacaoDTO localizacaoDTO) {
		Localizacao localizacao = localizacaoSearch.getLocalizacaoByCodigo(codigoLocalizacao);
		localizacao.setLocalizacao(localizacaoDTO.getNome());
		localizacao.setLocalizacaoPai(localizacaoSearch.getLocalizacaoByCodigo(localizacaoDTO.getCodigoLocalizacaoSuperior()));
		if (localizacaoDTO.getCodigoEstrutura() != null){
			localizacao.setEstruturaFilho(estruturaSearch.getEstruturaByNome(localizacaoDTO.getCodigoEstrutura()));
		}
		return new LocalizacaoDTO(localizacaoManager.update(localizacao));
	}
	

	public void removerLocalizacao(String codigoLocalizacao) {
		Localizacao localizacao = localizacaoSearch.getLocalizacaoByCodigo(codigoLocalizacao);
		localizacao.setAtivo(Boolean.FALSE);
		localizacaoManager.update(localizacao);
	}

	public LocalizacaoDTO getLocalizacao(String codigoLocalizacao) {
		return new LocalizacaoDTO(localizacaoSearch.getLocalizacaoByCodigo(codigoLocalizacao));
	}

	public List<LocalizacaoDTO> getLocalizacoes() {
		return localizacaoDTOSearch.getLocalizacaoDTOList();
	}

}
