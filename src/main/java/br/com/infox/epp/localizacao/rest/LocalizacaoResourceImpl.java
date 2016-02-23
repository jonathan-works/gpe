package br.com.infox.epp.localizacao.rest;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class LocalizacaoResourceImpl implements LocalizacaoResource {

	@Inject
	private LocalizacaoRestService localizacaoRestService;

	private String codigoLocalizacao;

	@Override
	public void setCodigoLocalizacao(String codigoLocalizacao) {
		this.codigoLocalizacao = codigoLocalizacao;
	}
	
	@Override
	public Response remover() {
		localizacaoRestService.removerLocalizacao(codigoLocalizacao);
		return Response.ok().build();
	}

	@Override
	public Response atualizar(LocalizacaoDTO localizacaoDTO) {
		localizacaoRestService.atualizarLocalizacao(codigoLocalizacao, localizacaoDTO);
		return Response.ok().build();
	}

	@Override
	public Response getLocalizacao() {
		return Response.ok().entity(localizacaoRestService.getLocalizacao(codigoLocalizacao)).build();
	}

}
