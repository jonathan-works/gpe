package br.com.infox.epp.entrega.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.entrega.CategoriaEntregaItemSearch;
import br.com.infox.epp.entrega.CategoriaEntregaSearch;
import br.com.infox.epp.entrega.CategoriaEntregaService;
import br.com.infox.epp.entrega.entity.CategoriaEntrega;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;

@Stateless
public class CategoriaEntregaRestService {

	@Inject
	private CategoriaEntregaService categoriaEntregaService;
	@Inject
	private CategoriaEntregaItemSearch categoriaEntregaItemSearch;	
	@Inject
	private CategoriaEntregaSearch categoriaEntregaSearch;	
	
	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}
	
	private static class ComparadorCategoriaEntrega implements Comparator<CategoriaEntrega> {

		@Override
		public int compare(CategoriaEntrega c1, CategoriaEntrega c2) {
			int retorno = c1.getDescricao().compareTo(c2.getDescricao());
			if(retorno == 0) {
				retorno = c1.getCodigo().compareTo(c2.getCodigo());
			}
			return retorno;					
		}
	}
	
	private void adicionarItens(Map<CategoriaEntrega, Categoria> mapaCategorias, Collection<CategoriaEntregaItem> itens) {
		for(CategoriaEntregaItem categoriaEntregaItemFilho : itens) {
			CategoriaEntrega categoriaEntrega = categoriaEntregaItemFilho.getCategoriaEntrega();
			Categoria categoria = mapaCategorias.get(categoriaEntrega);
			if(categoria == null) {
				categoria = toCategoria(categoriaEntrega);
				mapaCategorias.put(categoriaEntrega, categoria);
			}
			
			Item item = toItem(categoriaEntregaItemFilho);
			categoria.getItens().add(item);
		}
	}
	
	private void adicionarCategorias(Map<CategoriaEntrega, Categoria> mapaCategorias, Collection<CategoriaEntrega> categorias) {
		for(CategoriaEntrega categoriaFilha : categorias) {
			Categoria categoria = mapaCategorias.get(categoriaFilha);
			if(categoria == null) {
				categoria = toCategoria(categoriaFilha);
				mapaCategorias.put(categoriaFilha, categoria);
			}
		}
	}
	
	private Categoria toCategoria(CategoriaEntrega categoriaEntrega) {
		Categoria categoria = new Categoria();
		categoria.setCodigo(categoriaEntrega.getCodigo());
		categoria.setDescricao(categoriaEntrega.getDescricao());
		return categoria;
	}
	
	private Item toItem(CategoriaEntregaItem categoriaEntregaItem) {
		Item item = new Item();
		item.setCodigo(categoriaEntregaItem.getCodigo());
		item.setDescricao(categoriaEntregaItem.getDescricao());
		return item;
	}
	
	public Categoria findByCodigo(String codigo, String codigoItemPai) {
		CategoriaEntrega categoria = categoriaEntregaSearch.getCategoriaEntregaByCodigo(codigo);
		Collection<CategoriaEntregaItem> itens = null;
		if(codigoItemPai == null) {
			itens = categoria.getItemsFilhos();
		}
		else {
			itens = categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigoPaiAndCodigoCategoria(codigoItemPai, codigo);
		}
		
		SortedMap<CategoriaEntrega, Categoria> mapaCategorias = new TreeMap<>(new ComparadorCategoriaEntrega());
		adicionarItens(mapaCategorias, itens);
		
		List<Categoria> categorias = new ArrayList<>(mapaCategorias.values());
		if(categorias.isEmpty()) {
			return toCategoria(categoria);
		}
		return categorias.iterator().next();
	}	
	
	/**
	 * Lista todas as categorias
	 */
	public List<Categoria> listCategorias() {
		List<CategoriaEntrega> categorias = categoriaEntregaSearch.list();
		
		return toCategorias(categorias);		
	}


	/**
	 * Lista as categorias do primeiro nível
	 */
	public List<Categoria> getCategoriasRoot() {
		List<CategoriaEntrega> categoriasRoot = categoriaEntregaSearch.getCategoriaEntregaRoot();

		return toCategorias(categoriasRoot);
	}

	private List<Categoria> toCategorias(Collection<CategoriaEntrega> categorias) {
		List<CategoriaEntregaItem> itens = new ArrayList<>();
		for(CategoriaEntrega categoria : categorias) {
			itens.addAll(categoria.getItemsFilhos());
		}
		SortedMap<CategoriaEntrega, Categoria> mapaCategorias = new TreeMap<>(new ComparadorCategoriaEntrega());
		
		adicionarCategorias(mapaCategorias, categorias);
		adicionarItens(mapaCategorias, itens);
		
		return new ArrayList<>(mapaCategorias.values());
	}
	
	public List<Categoria> getCategoriasFilhas(String codigoItemPai) {
		Integer idItemPai = codigoItemPai == null ? null : categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(codigoItemPai).getId();
		return getCategoriasFilhas(idItemPai);
	}
	
	/**
	 * Retorna uma lista contendo todos as categorias filhas do item com o código informado
	 * @param codigoItemPai Código do item cujas filhas serão listadas ou nulo caso devam ser retornados as categorias raiz 
	 * @return
	 */
	public List<Categoria> getCategoriasFilhas(Integer idItemPai) {
		if(idItemPai == null) {
			return getCategoriasRoot();
		}
		CategoriaEntregaItem categoriaEntregaItem = categoriaEntregaItemSearch.getEntityManager().find(CategoriaEntregaItem.class, idItemPai);
		List<CategoriaEntregaItem> categoriaEntregaItens = categoriaEntregaService.getItensFilhos(categoriaEntregaItem.getCodigo());
		SortedMap<CategoriaEntrega, Categoria> mapaCategorias = new TreeMap<>(new ComparadorCategoriaEntrega());
		
		//Adiciona categorias e itens filhos do item informado
		adicionarItens(mapaCategorias, categoriaEntregaItens);
		//Adiciona as categorias filhas da categoria do item
		adicionarCategorias(mapaCategorias, categoriaEntregaItem.getCategoriaEntrega().getCategoriasFilhas());
		
		return new ArrayList<>(mapaCategorias.values());
	}
	
		
	public CategoriaEntrega novaCategoria(Categoria categoria, String codigoItemPai) {
		Integer idItemPai = codigoItemPai == null ? null : categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(codigoItemPai).getId();
		return novaCategoria(categoria, idItemPai);
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
		
	public void remover(String codigoCategoria) {
		CategoriaEntrega categoria = categoriaEntregaSearch.getCategoriaEntregaByCodigo(codigoCategoria);
		getEntityManager().remove(categoria);
		getEntityManager().flush();
	}	
}
