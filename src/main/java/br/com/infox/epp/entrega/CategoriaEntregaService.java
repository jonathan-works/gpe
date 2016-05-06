package br.com.infox.epp.entrega;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.entrega.entity.CategoriaEntrega;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.entity.CategoriaItemRelacionamento;

@Stateless
public class CategoriaEntregaService {
	
	@Inject
	private CategoriaEntregaItemSearch categoriaEntregaItemSearch;
	@Inject
	private CategoriaEntregaSearch categoriaEntregaSearch;
	
	/**
	 * Retorna uma lista contendo todos os itens filhos do item com o código informado
	 * @param codigoItemPai Código do item cujos filhos serão listados ou nulo caso devam ser retornados os itens raiz 
	 * @return
	 */
	public List<CategoriaEntregaItem> getItensFilhos(String codigoItemPai) {
		List<CategoriaEntregaItem> retorno = new ArrayList<>();
		if(codigoItemPai == null) {
			for(CategoriaEntrega categoriaEntrega : getCategoriasEntregaRoot()) {
				for(CategoriaEntregaItem item : categoriaEntrega.getItemsFilhos()) {
					retorno.add(item);
				}
			}
			return retorno;
		}
		
		CategoriaEntregaItem categoriaEntregaItem = categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(codigoItemPai);
		for(CategoriaItemRelacionamento item : categoriaEntregaItem.getItemsFilhos()) {
			retorno.add(item.getItemFilho());
		}
		return retorno;
	}
	
	public List<CategoriaEntrega> getCategoriasEntregaRoot() {
		return categoriaEntregaSearch.getCategoriaEntregaRoot();
	}
	
}
