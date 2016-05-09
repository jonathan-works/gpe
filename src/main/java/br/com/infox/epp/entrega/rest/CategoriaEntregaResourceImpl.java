package br.com.infox.epp.entrega.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.infox.epp.cdi.config.BeanManager;

public class CategoriaEntregaResourceImpl implements CategoriaEntregaResource {

	@Inject
	private CategoriaEntregaRestService categoriaEntregaRestService;
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
		categoriaEntregaRestService.novaCategoria(categoria, codigoItemPai);
		return Response.ok().status(Status.CREATED).build();
	}
	
	@Override
	public CategoriaEntregaItemResource getItem(String codigo) 
	{
		CategoriaEntregaItemResourceImpl itemResource = BeanManager.INSTANCE.getReference(CategoriaEntregaItemResourceImpl.class);
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

}
