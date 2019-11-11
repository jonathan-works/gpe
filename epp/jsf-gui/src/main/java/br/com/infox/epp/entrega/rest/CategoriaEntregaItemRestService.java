package br.com.infox.epp.entrega.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.entrega.CategoriaEntregaItemService;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.entity.CategoriaItemRelacionamento;

@Stateless
public class CategoriaEntregaItemRestService {

	@Inject
	private CategoriaEntregaItemService categoriaEntregaItemService;
	
	private Item toItem(CategoriaEntregaItem categoriaEntregaItem) {
		Item item = new Item();
		item.setCodigo(categoriaEntregaItem.getCodigo());
		item.setDescricao(categoriaEntregaItem.getDescricao());
		return item;
	}
	
	public Item findByCodigo(String codigo) {
		return toItem(categoriaEntregaItemService.getItem(codigo));		
	}
	
	private List<Item> toList(List<CategoriaEntregaItem> itens) {
		if(itens == null) {
			return null;
		}
		SortedSet<Item> setItens = new TreeSet<>();
		for(CategoriaEntregaItem item : itens) {
			setItens.add(toItem(item));
		}
		List<Item> retorno = new ArrayList<>();
		retorno.addAll(setItens);
		return retorno;
	}
	
	public List<Item> getFilhos(String codigoItemPai, String codigoCategoriaPai) {
		return toList(categoriaEntregaItemService.getFilhos(codigoItemPai, codigoCategoriaPai));
	}
	
	public CategoriaItemRelacionamento remover(String codigoItem, String codigoItemPai) {
		return categoriaEntregaItemService.remover(codigoItem, codigoItemPai);
	}
	
	public CategoriaEntregaItem novo(Item item, String codigoItemPai, String codigoCategoria) {
		CategoriaEntregaItem itemBanco = new CategoriaEntregaItem();
		itemBanco.setCodigo(item.getCodigo());
		itemBanco.setDescricao(item.getDescricao());
		
		categoriaEntregaItemService.novo(itemBanco, codigoItemPai, codigoCategoria);
		
		return itemBanco;
	}
	
	public void atualizar(String codigoCategoria, String novaDescricao) {
		categoriaEntregaItemService.atualizar(codigoCategoria, novaDescricao);
	}
	
	public void relacionarItens(String codigoItemPai, String codigoItem) {
		categoriaEntregaItemService.relacionarItens(codigoItemPai, codigoItem);
	}
	
	public List<Item> localizarItensCategoriaContendoDescricao(String codigoCategoria, String descricao) {
		return toList(categoriaEntregaItemService.localizarItensCategoriaContendoDescricao(codigoCategoria, descricao));
	}
}
