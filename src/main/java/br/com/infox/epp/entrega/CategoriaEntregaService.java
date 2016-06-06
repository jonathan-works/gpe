package br.com.infox.epp.entrega;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.google.common.base.Strings;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.exception.EppConfigurationException;
import br.com.infox.epp.entrega.entity.CategoriaEntrega;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.seam.exception.BusinessException;

@Stateless
public class CategoriaEntregaService {

	@Inject
	private CategoriaEntregaSearch categoriaEntregaSearch;
	@Inject
	private CategoriaEntregaItemSearch categoriaEntregaItemSearch;
	@Inject
	private ParametroManager parametroManager;

	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

	private CategoriaEntregaItem getItem(String codigo) {
		try {
			return categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(codigo);
		} catch (NoResultException e) {
			throw new BusinessException("Item com código " + codigo + " não encontrado");
		}
	}

	public CategoriaEntrega getCategoria(String codigo) {
		try {
			return categoriaEntregaSearch.getCategoriaEntregaByCodigo(codigo);
		} catch (NoResultException e) {
			throw new BusinessException("Categoria com código " + codigo + " não encontrada");
		}
	}

	public void atualizar(String codigoCategoria, String novaDescricao) {
		CategoriaEntrega categoria = getCategoria(codigoCategoria);
		categoria.setDescricao(novaDescricao);
		getEntityManager().flush();
	}

	public void novaCategoria(CategoriaEntrega categoria, String codigoItemPai) {
		if (codigoItemPai != null) {
			CategoriaEntregaItem itemPai = getItem(codigoItemPai);
			categoria.setCategoriaEntregaPai(itemPai.getCategoriaEntrega());
		}

		getEntityManager().persist(categoria);
		getEntityManager().flush();
	}

	/**
	 * Lista as categorias do primeiro nível
	 */
	public List<CategoriaEntrega> getCategoriasRoot() {
		return categoriaEntregaSearch.getCategoriaEntregaRoot();
	}

	public void remover(String codigoCategoria) {
		CategoriaEntrega categoria = categoriaEntregaSearch.getCategoriaEntregaByCodigo(codigoCategoria);
		getEntityManager().remove(categoria);
		getEntityManager().flush();
	}

	public List<CategoriaEntrega> getCategoriasFilhas(String codigoItemPai) {
		if (codigoItemPai == null) {
			return getCategoriasRoot();
		}
		CategoriaEntregaItem itemPai = getItem(codigoItemPai);

		return new ArrayList<>(itemPai.getCategoriaEntrega().getCategoriasFilhas());
	}

	/**
	 * Lista todas as categorias
	 */
	public List<CategoriaEntrega> list() {
		return categoriaEntregaSearch.list();
	}
	
	public CategoriaEntrega getCategoriaByNomeParametro(String nomeParametro) {
		Parametro parametro = parametroManager.getParametro(nomeParametro);
		String codigo = parametro.getValorVariavel();
		if (Strings.isNullOrEmpty(codigo)) {
			throw new EppConfigurationException("O parâmetro " + nomeParametro + " não está configurado");
		}
		return getCategoria(codigo);
	}
}
