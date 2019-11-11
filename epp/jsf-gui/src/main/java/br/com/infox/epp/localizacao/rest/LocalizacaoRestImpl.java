package br.com.infox.epp.localizacao.rest;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public class LocalizacaoRestImpl implements LocalizacaoRest {

	@Inject
	private LocalizacaoResource localizacaoResource;
	@Inject
	private LocalizacaoRestService localizacaoRestService;

	@Override
	public Response adicionarLocalizacao(UriInfo uriInfo, LocalizacaoDTO localizacao) {
		LocalizacaoDTO novaLocalizacao = localizacaoRestService.adicionarLocalizacao(localizacao);
		URI location = uriInfo.getAbsolutePathBuilder().path(novaLocalizacao.getCodigo()).build();
        return Response.created(location).build();
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
