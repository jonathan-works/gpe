package br.com.infox.epp.entrega.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.entrega.CategoriaEntregaItemSearch;
import br.com.infox.epp.entrega.CategoriaEntregaSearch;
import br.com.infox.epp.entrega.CategoriaItemRelacionamentoSearch;
import br.com.infox.epp.entrega.entity.CategoriaEntrega;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.entity.CategoriaItemRelacionamento;

@Stateless
public class CategoriaEntregaItemRestService {

	@Inject
	private CategoriaEntregaItemSearch categoriaEntregaItemSearch;		
	@Inject
	private CategoriaEntregaSearch categoriaEntregaSearch;	
	@Inject
	private CategoriaItemRelacionamentoSearch categoriaItemRelacionamentoSearch;
	
	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}
	
	private Item toItem(CategoriaEntregaItem categoriaEntregaItem) {
		Item item = new Item();
		item.setCodigo(categoriaEntregaItem.getCodigo());
		item.setDescricao(categoriaEntregaItem.getDescricao());
		return item;
	}
	
	public Item findByCodigo(String codigo) {
		return toItem(categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(codigo));		
	}
	
	private List<Item> toList(List<CategoriaEntregaItem> itens) {
		if(itens == null) {
			return null;
		}
		List<Item> retorno = new ArrayList<>();
		for(CategoriaEntregaItem item : itens) {
			retorno.add(toItem(item));
		}
		return retorno;
	}
	
	public List<Item> getFilhos(String codigoItemPai, String codigoCategoriaPai) {
		List<CategoriaEntregaItem> retorno;
		
		if(codigoItemPai == null) {
			retorno = categoriaEntregaItemSearch.list();
		}
		else {
			retorno = new ArrayList<>();
			CategoriaEntregaItem itemPai = categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(codigoItemPai);
			for(CategoriaItemRelacionamento item : itemPai.getItensFilhos()) {
				retorno.add(item.getItemFilho());
			}
			
		}
		
		//Remove todos os itens com categoria diferente do código da categoria pai (caso tenha sido definido)
		if(codigoCategoriaPai != null) {
			Iterator<CategoriaEntregaItem> it = retorno.iterator();
			while(it.hasNext()) {
				CategoriaEntregaItem item = it.next();
				if(!codigoCategoriaPai.equals(item.getCategoriaEntrega().getCodigo())) {
					it.remove();
				}
			}
		}
		
		return toList(retorno);
	}
	
	public CategoriaItemRelacionamento remover(String codigoItem, String codigoItemPai) {
		if(codigoItemPai == null) {
			throw new RuntimeException("Não é possível remover um item raiz");
		}
		CategoriaItemRelacionamento categoriaItemRelacionamento = categoriaItemRelacionamentoSearch.getByCodigoPaiAndFilho(codigoItemPai, codigoItem);
		
		getEntityManager().remove(categoriaItemRelacionamento);
		getEntityManager().flush();
		return categoriaItemRelacionamento;
		
	}
	
	public CategoriaEntregaItem novo(Item item, String codigoItemPai, String codigoCategoria) {
		CategoriaEntrega categoria = categoriaEntregaSearch.getCategoriaEntregaByCodigo(codigoCategoria);
		Integer idItemPai = codigoItemPai == null ? null : categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(codigoItemPai).getId();
		return novo(item, idItemPai, categoria.getId());		
	}
	
	public CategoriaEntregaItem novo(Item item, Integer idItemPai, Integer idCategoria) {
		CategoriaEntregaItem itemPai = idItemPai == null ? null : getEntityManager().find(CategoriaEntregaItem.class, idItemPai);
		CategoriaEntrega categoria = getEntityManager().find(CategoriaEntrega.class, idCategoria);
		
		CategoriaEntregaItem itemBanco = new CategoriaEntregaItem();
		itemBanco.setCodigo(item.getCodigo());
		itemBanco.setDescricao(item.getDescricao());
		itemBanco.setCategoriaEntrega(categoria);
		getEntityManager().persist(itemBanco);
		
		CategoriaItemRelacionamento categoriaItemRelacionamento = new CategoriaItemRelacionamento();
		categoriaItemRelacionamento.setItemFilho(itemBanco);
		categoriaItemRelacionamento.setItemPai(itemPai);
		getEntityManager().persist(categoriaItemRelacionamento);
		getEntityManager().flush();
		
		return itemBanco;
	}
	

}
