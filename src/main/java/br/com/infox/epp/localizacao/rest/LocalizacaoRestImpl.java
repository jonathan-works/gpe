package br.com.infox.epp.localizacao.rest;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class LocalizacaoRestImpl implements LocalizacaoRest {

	@Inject
	private LocalizacaoResource localizacaoResource;
	@Inject
	private LocalizacaoRestService localizacaoRestService;

	@Override
	public Response adicionarLocalizacao(LocalizacaoDTO localizacao) {
		localizacaoRestService.adicionarLocalizacao(localizacao);
		return Response.ok().build();
	}

	@Override
	public Response getLocalizacoes() {
		return Response.ok().entity(localizacaoRestService.getLocalizacoes()).build();
	}

	@Override
	public LocalizacaoResource getLocalizacaoResource(String codigo) {
		localizacaoResource.setCodigoLocalizacao(codigo);
		return localizacaoResource;
	}

}
