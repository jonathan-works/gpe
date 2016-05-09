package br.com.infox.epp.entrega.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;

public class CategoriaEntregaItemResourceImpl implements CategoriaEntregaItemResource {

	@Inject
	private CategoriaEntregaItemRestService categoriaEntregaItemRestService;
	@Context
	private UriInfo uriInfo;
	private String codigoCategoria;
	private String codigoItemPai;

	@Override
	public List<Item> list() {
		return categoriaEntregaItemRestService.getFilhos(codigoItemPai, codigoCategoria);
	}
	
	@Override
	public Response novoItem(Item item)
	{
		CategoriaEntregaItem itemBanco = categoriaEntregaItemRestService.novo(item, codigoItemPai, codigoCategoria);
		URI location = uriInfo.getAbsolutePathBuilder().path(itemBanco.getCodigo()).build();
		return Response.created(location).build();
	}
	
	public void setCodigoCategoria(String codigoCategoria) {
		this.codigoCategoria = codigoCategoria;
	}

	public void setCodigoItemPai(String codigoItemPai) {
		this.codigoItemPai = codigoItemPai;
	}		

	@Override
	public CategoriaEntregaResource getCategoria(String codigoItemPai) {
		CategoriaEntregaResourceImpl categoriaEntregaResourceImpl = BeanManager.INSTANCE.getReference(CategoriaEntregaResourceImpl.class);
		categoriaEntregaResourceImpl.setCodigoItemPai(codigoItemPai);
		return categoriaEntregaResourceImpl;
	}

	@Override
	public CategoriaEntregaItemResource getItem(String codigoItemPai) {
		CategoriaEntregaItemResourceImpl itemResource = BeanManager.INSTANCE.getReference(CategoriaEntregaItemResourceImpl.class);
		itemResource.setCodigoItemPai(codigoItemPai);
		itemResource.setCodigoCategoria(codigoCategoria);
		return itemResource;
	}

	@Override
	public Item get(String codigo) {
		return categoriaEntregaItemRestService.findByCodigo(codigo);
	}

	@Override
	public void remove(String codigo) {
		categoriaEntregaItemRestService.remover(codigo, codigoItemPai);
	}

}
