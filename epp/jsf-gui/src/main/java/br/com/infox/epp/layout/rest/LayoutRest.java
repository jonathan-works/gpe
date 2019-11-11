package br.com.infox.epp.layout.rest;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.google.common.net.MediaType;

import br.com.infox.epp.layout.entity.ResourceBin.TipoArquivo;
import br.com.infox.epp.layout.manager.LayoutManager;
import br.com.infox.epp.layout.rest.entity.MetadadosResource;

@Path("skin/{codigoSkin}")
@Stateless
public class LayoutRest {

	private static final int TEMPO_MAXIMO_CACHE_MINUTOS = 30;
	
	@Inject
	LayoutManager servico;
	
	@Inject
	private Logger logger;

	@GET
	@Path("path/{path : .+}")
	public Response getResourceByPath(@PathParam("codigoSkin") String codigoSkin, @PathParam("path") String pathResource,
	        @Context Request request) {
		pathResource = "/" + pathResource;
		String codigo = servico.getCodigo(pathResource);
		return getResourceByCodigo(codigoSkin, codigo, request);
	}
	
	@GET
	@Path("{codigo : .+}")
	public Response getResourceByCodigo(@PathParam("codigoSkin") String codigoSkin, @PathParam("codigo") String codigoResource,
	        @Context Request request) {
		MetadadosResource metadados = servico.getMetadados(codigoSkin, codigoResource);
		EntityTag etag = metadados.getEtag();
		ResponseBuilder builder = request.evaluatePreconditions(etag);
		final CacheControl cacheControl = new CacheControl();
		cacheControl.setMaxAge((int)TimeUnit.MINUTES.toSeconds(TEMPO_MAXIMO_CACHE_MINUTOS));

		if (builder == null) {
			logger.info("Resource de código: "+ codigoResource+ " não encontrado na base binaria");
			byte[] resource = servico.carregarBinario(codigoSkin, codigoResource);
			TipoArquivo tipoResource = metadados.getTipo();
			String type = "image/" + tipoResource.toString().toLowerCase();
			if (tipoResource == TipoArquivo.SVG || tipoResource == TipoArquivo.SVGZ) {
				type = MediaType.SVG_UTF_8.toString();
			}
			builder = Response.ok(resource).type(type).tag(etag);
			if (tipoResource == TipoArquivo.SVGZ) {
				builder = builder.header("Content-Encoding", "x-gzip");
			}
		}

		return builder.cacheControl(cacheControl).build();
	}
	

}
