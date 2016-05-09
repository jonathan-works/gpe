package br.com.infox.epp.entrega.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.infox.epp.cdi.config.BeanManager;

public class CategoriaEntregaResourceImpl implements CategoriaEntregaResource {

	@Inject
	private CategoriaEntregaRestService categoriaEntregaRestService;
	private Integer idItemPai;
	
	public void setIdItemPai(Integer idItemPai) {
		this.idItemPai = idItemPai;
	}
	
	@Override
	public List<Categoria> getCategorias(Integer idItemPai)
	{
		if(idItemPai == null) {
			idItemPai = this.idItemPai;
		}
		
		if(idItemPai == null) {
			return categoriaEntregaRestService.listCategorias();			
		}
		else {
			return categoriaEntregaRestService.getCategoriasFilhas(idItemPai);			
		}
	}
	
	@Override
	public Response novaCategoria(Categoria categoria)
	{
		categoriaEntregaRestService.novaCategoria(categoria, idItemPai);
		return Response.ok().status(Status.CREATED).build();
	}
	
	@Override
	public CategoriaEntregaItemResource getItem(Integer id) 
	{
		CategoriaEntregaItemResourceImpl itemResource = BeanManager.INSTANCE.getReference(CategoriaEntregaItemResourceImpl.class);
		itemResource.setIdCategoria(id);
		itemResource.setIdItemPai(idItemPai);
		return itemResource;
	}

}
