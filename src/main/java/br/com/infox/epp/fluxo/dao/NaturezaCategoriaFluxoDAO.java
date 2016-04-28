package br.com.infox.epp.fluxo.dao;

import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.ATIVOS_BY_FLUXO;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.LIST_BY_NATUREZA;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.LIST_BY_RELATIONSHIP;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.NATCATFLUXO_BY_DS_NATUREZA_DS_CATEGORIA;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.NATCATFLUXO_BY_DS_NATUREZA_DS_CATEGORIA_DISPONIVEIS;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.PARAM_CATEGORIA;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.PARAM_DS_CATEGORIA;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.PARAM_DS_NATUREZA;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.PARAM_FLUXO;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.PARAM_NATUREZA;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Categoria_;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.fluxo.entity.Natureza_;

/**
 * Classe DAO para a entidade NaturezaCategoriaFluxo
 * 
 * @author Daniel
 * 
 */
@Name(NaturezaCategoriaFluxoDAO.NAME)
@AutoCreate
public class NaturezaCategoriaFluxoDAO extends DAO<NaturezaCategoriaFluxo> {

	private static final long serialVersionUID = -1456893293816945596L;
	public static final String NAME = "naturezaCategoriaFluxoDAO";

	/**
	 * Lista todos os registros filtrando por uma natureza.
	 * 
	 * @param natureza
	 *            que se desejar filtrar a seleção.
	 * @return lista de todos os registros referente a <code>natureza</code>
	 *         informada.
	 */
	public List<NaturezaCategoriaFluxo> listByNatureza(Natureza natureza) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(PARAM_NATUREZA, natureza);
		return getNamedResultList(LIST_BY_NATUREZA, parameters);
	}

	public NaturezaCategoriaFluxo getByRelationship(Natureza natureza, Categoria categoria, Fluxo fluxo) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(PARAM_NATUREZA, natureza);
		parameters.put(PARAM_CATEGORIA, categoria);
		parameters.put(PARAM_FLUXO, fluxo);
		return getNamedSingleResult(LIST_BY_RELATIONSHIP, parameters);
	}

	public List<NaturezaCategoriaFluxo> getActiveNaturezaCategoriaFluxoListByFluxo(Fluxo fluxo) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(PARAM_FLUXO, fluxo);
		return getNamedResultList(ATIVOS_BY_FLUXO, parameters);
	}

	public NaturezaCategoriaFluxo getNaturezaCategoriaFluxoByDsNatAndDsCat(String dsNatureza, String dsCategoria) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(PARAM_DS_NATUREZA, dsNatureza.toUpperCase());
		parameters.put(PARAM_DS_CATEGORIA, dsCategoria.toUpperCase());
		return getNamedSingleResult(NATCATFLUXO_BY_DS_NATUREZA_DS_CATEGORIA, parameters);
	}

	public NaturezaCategoriaFluxo getNaturezaCategoriaFluxoDisponiveis(String dsNatureza, String dsCategoria) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(PARAM_DS_NATUREZA, dsNatureza.toUpperCase());
		parameters.put(PARAM_DS_CATEGORIA, dsCategoria.toUpperCase());
		NaturezaCategoriaFluxo naturezaCategoriaFluxo = getNamedSingleResult(NATCATFLUXO_BY_DS_NATUREZA_DS_CATEGORIA_DISPONIVEIS,
				parameters);
		Date dataAtual = new Date();
		if (naturezaCategoriaFluxo != null && naturezaCategoriaFluxo.getFluxo().getDataInicioPublicacao().before(dataAtual) && 
			(naturezaCategoriaFluxo.getFluxo().getDataFimPublicacao() == null || naturezaCategoriaFluxo.getFluxo().getDataFimPublicacao().before(dataAtual))) {
			return naturezaCategoriaFluxo;
		}
		return null;
	}

	public NaturezaCategoriaFluxo getByCodigos(String descricaoNatureza, String descricaoCategoria, String codigoFluxo) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<NaturezaCategoriaFluxo> query = cb.createQuery(NaturezaCategoriaFluxo.class);
		Root<NaturezaCategoriaFluxo> ncf = query.from(NaturezaCategoriaFluxo.class);
		Join<NaturezaCategoriaFluxo, Natureza> natureza = ncf.join(NaturezaCategoriaFluxo_.natureza, JoinType.INNER);
		Join<NaturezaCategoriaFluxo, Categoria> categoria = ncf.join(NaturezaCategoriaFluxo_.categoria, JoinType.INNER);
		Join<NaturezaCategoriaFluxo, Fluxo> fluxo = ncf.join(NaturezaCategoriaFluxo_.fluxo, JoinType.INNER);
		query.where(
			cb.equal(natureza.get(Natureza_.natureza), descricaoNatureza),
			cb.equal(categoria.get(Categoria_.categoria), descricaoCategoria),
			cb.equal(fluxo.get(Fluxo_.codFluxo), codigoFluxo)
		);
		List<NaturezaCategoriaFluxo> result = entityManager.createQuery(query).setMaxResults(1).getResultList();
		if (result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
}
