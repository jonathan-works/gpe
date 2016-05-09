package br.com.infox.epp.entrega.rest;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;

public class CategoriaEntregaItemResourceImpl implements CategoriaEntregaItemResource {

	@Inject
	private CategoriaEntregaRestService categoriaEntregaRestService;
	@Context
	private UriInfo uriInfo;
	private Integer idCategoria;
	private Integer idItemPai;
	
	@POST
	public Response novoItem(Item item)
	{
		CategoriaEntregaItem itemBanco = categoriaEntregaRestService.novoItem(item, idItemPai, idCategoria);
		URI location = uriInfo.getAbsolutePathBuilder().path(itemBanco.getCodigo()).build();
		return Response.created(location).build();
	}

	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}

	public void setIdItemPai(Integer idItemPai) {
		this.idItemPai = idItemPai;
	}

	@Override
	public CategoriaEntregaResource getCategoria(Integer id) {
		CategoriaEntregaResourceImpl categoriaEntregaResourceImpl = BeanManager.INSTANCE.getReference(CategoriaEntregaResourceImpl.class);
		categoriaEntregaResourceImpl.setIdItemPai(id);
		return categoriaEntregaResourceImpl;
	}

	@Override
	public CategoriaEntregaItemResource getItem(Integer id) {
		CategoriaEntregaItemResourceImpl itemResource = BeanManager.INSTANCE.getReference(CategoriaEntregaItemResourceImpl.class);
		itemResource.idItemPai = id;
		itemResource.idCategoria = idCategoria;
		return itemResource;
	}	
}
