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

import br.com.infox.epp.entrega.CategoriaEntregaItemService;
import br.com.infox.epp.entrega.CategoriaEntregaService;
import br.com.infox.epp.entrega.entity.CategoriaEntrega;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;

@Stateless
public class CategoriaEntregaRestService {

	@Inject
	private CategoriaEntregaService categoriaEntregaService;
	@Inject
	private CategoriaEntregaItemService categoriaEntregaItemService;
	
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
		CategoriaEntrega categoria = categoriaEntregaService.getCategoria(codigo);
		Collection<CategoriaEntregaItem> itens = categoriaEntregaItemService.getFilhos(codigoItemPai, codigo);
		
		SortedMap<CategoriaEntrega, Categoria> mapaCategorias = new TreeMap<>(new ComparadorCategoriaEntrega());
		adicionarItens(mapaCategorias, itens);
		
		List<Categoria> categorias = new ArrayList<>(mapaCategorias.values());
		if(categorias.isEmpty()) {
			return toCategoria(categoria);
		}
		return categorias.iterator().next();
	}	
	
	public List<Categoria> listCategorias() {
		List<CategoriaEntrega> categorias = categoriaEntregaService.list();
		
		return toCategorias(categorias);		
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
	
	/**
	 * Retorna uma lista contendo todos as categorias filhas do item com o código informado
	 * @param codigoItemPai Código do item cujas filhas serão listadas ou nulo caso devam ser retornados as categorias raiz 
	 * @return
	 */
	public List<Categoria> getCategoriasFilhas(String codigoItemPai) {
		List<CategoriaEntregaItem> itens = categoriaEntregaItemService.getItensFilhos(codigoItemPai);
		List<CategoriaEntrega> categorias = categoriaEntregaService.getCategoriasFilhas(codigoItemPai);
		
		SortedMap<CategoriaEntrega, Categoria> mapaCategorias = new TreeMap<>(new ComparadorCategoriaEntrega());
		
		//Adiciona categorias e itens filhos do item informado
		adicionarItens(mapaCategorias, itens);
		//Adiciona as categorias filhas da categoria do item
		adicionarCategorias(mapaCategorias, categorias);
		
		return new ArrayList<>(mapaCategorias.values());
	}
	
				
	public void remover(String codigoCategoria) {
		categoriaEntregaService.remover(codigoCategoria);
	}
	
	public void atualizar(String codigoCategoria, String novaDescricao) {
		categoriaEntregaService.atualizar(codigoCategoria, novaDescricao);
	}
	
	public CategoriaEntrega novaCategoria(Categoria categoria, String codigoItemPai) {
		CategoriaEntrega categoriaEntrega = new CategoriaEntrega();
		categoriaEntrega.setCodigo(categoria.getCodigo());
		categoriaEntrega.setDescricao(categoria.getDescricao());
		
		categoriaEntregaService.novaCategoria(categoriaEntrega, codigoItemPai);
		
		return categoriaEntrega;
	}
	
}
