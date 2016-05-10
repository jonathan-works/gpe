package br.com.infox.epp.entrega;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.entrega.entity.CategoriaEntrega;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.entity.CategoriaItemRelacionamento;
import br.com.infox.seam.exception.BusinessException;

@Stateless
public class CategoriaEntregaItemService {
	
	@Inject
	private CategoriaEntregaItemSearch categoriaEntregaItemSearch;
	@Inject
	private CategoriaEntregaService categoriaEntregaService;
	
	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}
	
	private CategoriaEntregaItem getItem(String codigo) {
		try {
			return categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(codigo);
		}
		catch(NoResultException e) {
			throw new BusinessException("Item com código " + codigo + " não encontrado");
		}
	}
	
	/**
	 * Retorna uma lista contendo todos os itens filhos do item com o código informado
	 * @param codigoItemPai Código do item cujos filhos serão listados ou nulo caso devam ser retornados os itens raiz 
	 * @return
	 */
	public List<CategoriaEntregaItem> getItensFilhos(String codigoItemPai) {
		List<CategoriaEntregaItem> retorno = new ArrayList<>();
		if(codigoItemPai == null) {
			for(CategoriaEntrega categoriaEntrega : categoriaEntregaService.getCategoriasEntregaRoot()) {
				for(CategoriaEntregaItem item : categoriaEntrega.getItemsFilhos()) {
					retorno.add(item);
				}
			}
			return retorno;
		}
		
		CategoriaEntregaItem categoriaEntregaItem = getItem(codigoItemPai);
		for(CategoriaItemRelacionamento item : categoriaEntregaItem.getItensFilhos()) {
			retorno.add(item.getItemFilho());
		}
		return retorno;
	}
	
	public void atualizar(String codigoItem, String novaDescricao) {
		CategoriaEntregaItem item = getItem(codigoItem);
		item.setDescricao(novaDescricao);
		getEntityManager().flush();
	}
	
	public void relacionarItens(String codigoItemPai, String codigoItem) {
		CategoriaEntregaItem itemPai = getItem(codigoItemPai);
		CategoriaEntregaItem item = getItem(codigoItem);
		CategoriaItemRelacionamento relacionamento = new CategoriaItemRelacionamento();
		relacionamento.setItemPai(itemPai);
		relacionamento.setItemFilho(item);
		getEntityManager().persist(relacionamento);
		getEntityManager().flush();
		
	}
	
	public List<CategoriaEntregaItem> localizarItensCategoriaContendoDescricao(String codigoCategoria, String descricao) {
		if(codigoCategoria == null) {
			return categoriaEntregaItemSearch.getCategoriaEntregaItemByDescricaoLike(descricao);
		}
		return categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigoCategoriaAndDescricaoLike(codigoCategoria, descricao);
	}
	
}
