package br.com.infox.epp.entrega;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.entrega.DetectorCiclo.Ciclo;
import br.com.infox.epp.entrega.DetectorCiclo.Ciclo.ToStringDelegate;
import br.com.infox.epp.entrega.DetectorCiclo.RelacionamentoSource;
import br.com.infox.epp.entrega.entity.CategoriaEntrega;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.entity.CategoriaItemRelacionamento;
import br.com.infox.seam.exception.BusinessException;

@Stateless
public class CategoriaEntregaItemService {

	@Inject
	private CategoriaEntregaItemSearch categoriaEntregaItemSearch;
	@Inject
	private CategoriaEntregaSearch categoriaEntregaSearch;
	@Inject
	private CategoriaEntregaService categoriaEntregaService;
	@Inject
	private CategoriaItemRelacionamentoSearch categoriaItemRelacionamentoSearch;
	@Inject
	private LocalizacaoManager localizacaoManager;

	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

	public CategoriaEntregaItem getItem(String codigo) {
		try {
			return categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(codigo);
		} catch (NoResultException e) {
			throw new BusinessException("Item com código " + codigo + " não encontrado");
		}
	}

	/**
	 * Retorna uma lista contendo todos os itens filhos do item com o código
	 * informado
	 * 
	 * @param codigoItemPai
	 *            Código do item cujos filhos serão listados ou nulo caso devam
	 *            ser retornados os itens raiz
	 * @return
	 */
	public List<CategoriaEntregaItem> getItensFilhos(String codigoItemPai) {
		List<CategoriaEntregaItem> retorno = new ArrayList<>();
		if (codigoItemPai == null) {
			for (CategoriaEntrega categoriaEntrega : categoriaEntregaService.getCategoriasRoot()) {
				for (CategoriaEntregaItem item : categoriaEntrega.getItemsFilhos()) {
					retorno.add(item);
				}
			}
			return retorno;
		}

		CategoriaEntregaItem categoriaEntregaItem = getItem(codigoItemPai);
		for (CategoriaItemRelacionamento item : categoriaEntregaItem.getItensFilhos()) {
			retorno.add(item.getItemFilho());
		}
		return retorno;
	}

	public void atualizar(String codigoItem, String novaDescricao) {
		CategoriaEntregaItem item = getItem(codigoItem);
		item.setDescricao(novaDescricao);
		getEntityManager().flush();
	}

	private Ciclo<CategoriaEntregaItem> getCiclo(CategoriaEntregaItem itemPai, CategoriaEntregaItem itemFilho) {
		RelacionamentoSource<CategoriaEntregaItem> source = new RelacionamentoSource<CategoriaEntregaItem>() {

			@Override
			public Collection<CategoriaEntregaItem> getPais(CategoriaEntregaItem item) {
				Collection<CategoriaEntregaItem> retorno = new HashSet<>();
				if (item == null) {
					return retorno;
				}
				for (CategoriaItemRelacionamento rel : item.getItensPais()) {
					retorno.add(rel.getItemPai());
				}
				return retorno;
			}

			@Override
			public Collection<CategoriaEntregaItem> getFilhos(CategoriaEntregaItem item) {
				Collection<CategoriaEntregaItem> retorno = new HashSet<>();
				if (item == null) {
					return retorno;
				}
				for (CategoriaItemRelacionamento rel : item.getItensFilhos()) {
					retorno.add(rel.getItemFilho());
				}
				return retorno;
			}
		};

		DetectorCiclo<CategoriaEntregaItem> detectorCiclo = new DetectorCiclo<>(source);

		return detectorCiclo.getCiclo(itemPai, itemFilho);
	}

	public void relacionarItens(String codigoItemPai, String codigoItem) {
		// Verifica se os itens já estão relacionados
		try {
			categoriaItemRelacionamentoSearch.getByCodigoPaiAndFilho(codigoItemPai, codigoItem);
			throw new BusinessException("Itens já estão relacionados");
		} catch (NoResultException e) {
		}
		CategoriaEntregaItem itemPai = getItem(codigoItemPai);
		CategoriaEntregaItem item = getItem(codigoItem);

		Ciclo<CategoriaEntregaItem> ciclo = getCiclo(itemPai, item);
		if (ciclo != null) {
			String strCiclo = ciclo.toString(new ToStringDelegate<CategoriaEntregaItem>() {
				@Override
				public String toString(CategoriaEntregaItem item) {
					return item.getCodigo();
				}
			});
			throw new BusinessException("Ciclo detectado: " + strCiclo);
		}

		CategoriaItemRelacionamento relacionamento = new CategoriaItemRelacionamento();
		relacionamento.setItemPai(itemPai);
		relacionamento.setItemFilho(item);
		getEntityManager().persist(relacionamento);
		getEntityManager().flush();
	}

	public List<CategoriaEntregaItem> localizarItensCategoriaContendoDescricao(String codigoCategoria,
			String descricao) {
		if (codigoCategoria == null) {
			return categoriaEntregaItemSearch.getCategoriaEntregaItemByDescricaoLike(descricao);
		}
		return categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigoCategoriaAndDescricaoLike(codigoCategoria,
				descricao);
	}

	/**
	 * Remove o relacionamento entre dos itens
	 */
	public CategoriaItemRelacionamento remover(String codigoItem, String codigoItemPai) {
		if (codigoItemPai == null) {
		    CategoriaItemRelacionamento rel;
		    try {
		        rel = categoriaItemRelacionamentoSearch.getByCodigoPaiAndFilho(codigoItemPai, codigoItem);
		    } catch (NoResultException e){
		        throw new BusinessException(DAOException.MSG_FOREIGN_KEY_VIOLATION);
		    }
		    CategoriaEntregaItem item = getItem(codigoItem);
		    getEntityManager().remove(rel);
		    getEntityManager().remove(item);
		    getEntityManager().flush();
			return null;
		}
		getItem(codigoItem);
		getItem(codigoItemPai);
		try {
			CategoriaItemRelacionamento categoriaItemRelacionamento = categoriaItemRelacionamentoSearch
					.getByCodigoPaiAndFilho(codigoItemPai, codigoItem);
			getEntityManager().remove(categoriaItemRelacionamento);
			getEntityManager().flush();
			return categoriaItemRelacionamento;
		} catch (NoResultException e) {
			throw new RuntimeException("Relacionamento não encontrado");
		}
	}

	public List<CategoriaEntregaItem> getFilhos(String codigoItemPai, String codigoCategoria) {
		if (codigoItemPai != null && codigoCategoria != null) {
			return categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigoPaiAndCodigoCategoria(codigoItemPai,
					codigoCategoria);
		} else if (codigoItemPai != null) {
			return categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigoPai(codigoItemPai);
		} else if (codigoCategoria != null) {
			return categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigoCategoria(codigoCategoria);
		} else {
			return categoriaEntregaItemSearch.list();
		}
	}

	public void novo(CategoriaEntregaItem item, String codigoItemPai, String codigoCategoria) {
		if (codigoCategoria == null) {
			throw new BusinessException("'codigoCategoria' deve ser informado");
		}
		CategoriaEntrega categoria = null;
		try {
			categoria = categoriaEntregaSearch.getCategoriaEntregaByCodigo(codigoCategoria);
		} catch (NoResultException e) {
			throw new BusinessException("Categoria com código " + codigoCategoria + " não encontrada");
		}
		CategoriaEntregaItem itemPai = codigoItemPai == null ? null : getItem(codigoItemPai);

		item.setCategoriaEntrega(categoria);

		getEntityManager().persist(item);

		CategoriaItemRelacionamento categoriaItemRelacionamento = new CategoriaItemRelacionamento();
		categoriaItemRelacionamento.setItemFilho(item);
		categoriaItemRelacionamento.setItemPai(itemPai);
		getEntityManager().persist(categoriaItemRelacionamento);
		getEntityManager().flush();
	}

    public List<CategoriaEntregaItem> getItensFilhosComDescricao(String codigoItemPai, String query) {
        return categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigoPaiAndDescricao(codigoItemPai, query);
    }
    
    public List<CategoriaEntregaItem> getByCodigoCategoriaAndCodigoLocalizacao(String codigoCategoria, String codigoLocalizacao) {
    	CategoriaEntrega categoriaEntrega = categoriaEntregaSearch.getCategoriaEntregaByCodigo(codigoCategoria);
    	Localizacao localizacao = localizacaoManager.getLocalizacaoByCodigo(codigoLocalizacao);
    	return categoriaEntregaItemSearch.getByCategoriaAndLocalizacao(categoriaEntrega, localizacao);
    }

}
