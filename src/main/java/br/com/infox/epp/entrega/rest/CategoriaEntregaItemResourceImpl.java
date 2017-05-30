package br.com.infox.epp.entrega.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import br.com.infox.core.token.AccessTokenAuthentication;
import br.com.infox.core.token.TokenRequester;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;

@AccessTokenAuthentication(TokenRequester.UNSPECIFIED)
public class CategoriaEntregaItemResourceImpl implements CategoriaEntregaItemResource {

	@Inject
	private CategoriaEntregaItemRestService categoriaEntregaItemRestService;
	@Context
	private UriInfo uriInfo;
	private String codigoCategoria;
	private String codigoItemPai;

	@Override
	public List<Item> list(String nomeLike) {
		if(nomeLike != null) {
			return categoriaEntregaItemRestService.localizarItensCategoriaContendoDescricao(codigoCategoria, nomeLike);			
		}
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
		CategoriaEntregaResourceImpl categoriaEntregaResourceImpl = Beans.getReference(CategoriaEntregaResourceImpl.class);
		categoriaEntregaResourceImpl.setCodigoItemPai(codigoItemPai);
		return categoriaEntregaResourceImpl;
	}

	@Override
	public CategoriaEntregaItemResource getItem(String codigoItemPai) {
		CategoriaEntregaItemResourceImpl itemResource = Beans.getReference(CategoriaEntregaItemResourceImpl.class);
		itemResource.setCodigoItemPai(codigoItemPai);
		itemResource.setCodigoCategoria(null);
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

	@Override
	public Response atualizar(String codigo, Item item) {
		categoriaEntregaItemRestService.atualizar(codigo, item.getDescricao());
		return Response.noContent().build();
	}

	@Override
	public Response adicionarRelacionamento(String codigo) {
		categoriaEntregaItemRestService.relacionarItens(codigoItemPai, codigo);
		return Response.noContent().build();
	}

}
