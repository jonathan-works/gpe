package br.com.infox.epp.entrega;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.entrega.entity.CategoriaEntrega;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.rest.Categoria;
import br.com.infox.seam.exception.BusinessException;

@Stateless
public class CategoriaEntregaService {
	
	@Inject
	private CategoriaEntregaSearch categoriaEntregaSearch;
	@Inject
	private CategoriaEntregaItemSearch categoriaEntregaItemSearch;
	
	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}
	
	public List<CategoriaEntrega> getCategoriasEntregaRoot() {
		return categoriaEntregaSearch.getCategoriaEntregaRoot();
	}
	
	private CategoriaEntrega getCategoria(String codigo) {
		try {
			return categoriaEntregaSearch.getCategoriaEntregaByCodigo(codigo);
		}
		catch(NoResultException e) {
			throw new BusinessException("Categoria com código " + codigo + " não encontrada");
		}
	}
	
	public void atualizar(String codigoCategoria, String novaDescricao) {
		CategoriaEntrega categoria = getCategoria(codigoCategoria);
		categoria.setDescricao(novaDescricao);
		getEntityManager().flush();
	}	
	
	public CategoriaEntrega novaCategoria(Categoria categoria, Integer idItemPai) {
		if(idItemPai == null) {
			CategoriaEntrega categoriaEntrega = new CategoriaEntrega();
			categoriaEntrega.setCodigo(categoria.getCodigo());
			categoriaEntrega.setDescricao(categoria.getDescricao());
			getEntityManager().persist(categoriaEntrega);
			getEntityManager().flush();
			return categoriaEntrega;
		}
		CategoriaEntregaItem categoriaEntregaItem = categoriaEntregaItemSearch.getEntityManager().find(CategoriaEntregaItem.class, idItemPai);
		CategoriaEntrega categoriaEntrega = new CategoriaEntrega();
		categoriaEntrega.setCodigo(categoria.getCodigo());
		categoriaEntrega.setDescricao(categoria.getDescricao());
		categoriaEntrega.setCategoriaEntregaPai(categoriaEntregaItem.getCategoriaEntrega());
		getEntityManager().persist(categoriaEntrega);
		getEntityManager().flush();
		
		return categoriaEntrega;		
	}	
}
