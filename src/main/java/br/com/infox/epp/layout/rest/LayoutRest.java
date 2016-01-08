package br.com.infox.epp.layout.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.google.common.net.MediaType;

import br.com.infox.epp.layout.entity.ResourceBin.TipoResource;
import br.com.infox.epp.layout.manager.LayoutManager;
import br.com.infox.epp.layout.rest.entity.MetadadosResource;

@Path("skin/{codigo}")
@Stateless
public class LayoutRest {

	@Inject
	LayoutManager servico;

	@GET
	@Path("{path : .+}")
	public Response getResource(@PathParam("codigo") String codigoSkin, @PathParam("path") String pathRecurso,
	        @Context Request request) {
		pathRecurso = "/" + pathRecurso;
		MetadadosResource metadados = servico.getMetadados(codigoSkin, pathRecurso);
		EntityTag etag = metadados.getEtag();
		ResponseBuilder builder = request.evaluatePreconditions(etag);

		if (builder == null) {
			byte[] resource = servico.carregarBinario(codigoSkin, pathRecurso);
			TipoResource tipoResource = metadados.getTipo();
			String type = "image/" + tipoResource.toString().toLowerCase();
			if (tipoResource == TipoResource.SVG || tipoResource == TipoResource.SVGZ) {
				type = MediaType.SVG_UTF_8.toString();
			}
			builder = Response.ok(resource).type(type).tag(etag);
			if (tipoResource == TipoResource.SVGZ) {
				builder = builder.header("Content-Encoding", "x-gzip");
			}
		}

		return builder.build();
	}

}
