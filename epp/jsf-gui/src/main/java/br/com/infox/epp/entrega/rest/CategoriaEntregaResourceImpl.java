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
import br.com.infox.epp.entrega.entity.CategoriaEntrega;

@AccessTokenAuthentication(TokenRequester.UNSPECIFIED)
public class CategoriaEntregaResourceImpl implements CategoriaEntregaResource {

	@Inject
	private CategoriaEntregaRestService categoriaEntregaRestService;
	@Context
    private UriInfo uriInfo;
	private String codigoItemPai;
	
	public void setCodigoItemPai(String codigoItemPai) {
		this.codigoItemPai = codigoItemPai;
	}
	
	@Override
	public List<Categoria> getCategorias()
	{
		return categoriaEntregaRestService.getCategoriasFilhas(codigoItemPai);			
	}
	
	@Override
	public Response novaCategoria(Categoria categoria)
	{
		CategoriaEntrega novaCategoria = categoriaEntregaRestService.novaCategoria(categoria, codigoItemPai);
		URI location = uriInfo.getAbsolutePathBuilder().path(novaCategoria.getCodigo()).build();
		return Response.created(location).build();
	}
	
	@Override
	public CategoriaEntregaItemResource getItem(String codigo) 
	{
		CategoriaEntregaItemResourceImpl itemResource = Beans.getReference(CategoriaEntregaItemResourceImpl.class);
		itemResource.setCodigoCategoria(codigo);
		itemResource.setCodigoItemPai(codigoItemPai);
		return itemResource;
	}

	@Override
	public Categoria get(String codigo) {
		return categoriaEntregaRestService.findByCodigo(codigo, codigoItemPai);
	}

	@Override
	public void remove(String codigo) {
		categoriaEntregaRestService.remover(codigo);
	}

	@Override
	public Response atualizar(Categoria categoria) {
		categoriaEntregaRestService.atualizar(categoria.getCodigo(), categoria.getDescricao());
		return Response.noContent().build();
	}

}
