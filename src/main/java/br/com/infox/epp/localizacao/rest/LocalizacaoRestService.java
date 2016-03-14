package br.com.infox.epp.localizacao.rest;

import static br.com.infox.epp.ws.RestUtils.produceErrorJson;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.infox.core.persistence.DAOException;
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
		
		try {
			localizacaoManager.persist(localizacao);
		} catch (DAOException e) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(produceErrorJson(e.getMessage())).build());
		}
		
		return new LocalizacaoDTO(localizacao);
	}

	public LocalizacaoDTO atualizarLocalizacao(String codigoLocalizacao, LocalizacaoDTO localizacaoDTO) {
		Localizacao localizacao = localizacaoSearch.getLocalizacaoByCodigo(codigoLocalizacao);
		localizacao.setLocalizacao(localizacaoDTO.getNome());
		localizacao.setLocalizacaoPai(localizacaoSearch.getLocalizacaoByCodigo(localizacaoDTO.getCodigo()));
		if (localizacaoDTO.getCodigoEstrutura() != null){
			localizacao.setEstruturaFilho(estruturaSearch.getEstruturaByNome(localizacaoDTO.getCodigoEstrutura()));
		}
		try {
			return new LocalizacaoDTO(localizacaoManager.update(localizacao));
		} catch (DAOException e) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(produceErrorJson(e.getMessage())).build());
		}
	}
	

	public void removerLocalizacao(String codigoLocalizacao) {
		Localizacao localizacao = localizacaoSearch.getLocalizacaoByCodigo(codigoLocalizacao);
		localizacao.setAtivo(Boolean.FALSE);
		try {
			localizacaoManager.update(localizacao);
		} catch (DAOException e) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(produceErrorJson(e.getMessage())).build());
		}
	}

	public LocalizacaoDTO getLocalizacao(String codigoLocalizacao) {
		return new LocalizacaoDTO(localizacaoSearch.getLocalizacaoByCodigo(codigoLocalizacao));
	}

	public List<LocalizacaoDTO> getLocalizacoes() {
		return localizacaoDTOSearch.getLocalizacaoDTOList();
	}

}
