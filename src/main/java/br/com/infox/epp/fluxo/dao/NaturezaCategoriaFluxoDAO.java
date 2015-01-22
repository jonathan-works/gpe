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

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;

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
}
