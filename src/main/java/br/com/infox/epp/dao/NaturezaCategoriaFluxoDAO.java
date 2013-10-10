package br.com.infox.epp.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.entity.Natureza;
import br.com.infox.epp.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.query.NaturezaCategoriaFluxoQuery;

/**
 * Classe DAO para a entidade NaturezaCategoriaFluxo
 * @author Daniel
 *
 */
@Name(NaturezaCategoriaFluxoDAO.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class NaturezaCategoriaFluxoDAO extends GenericDAO {

	private static final long serialVersionUID = -1456893293816945596L;
	public static final String NAME = "naturezaCategoriaFluxoDAO";

	/**
	 * Lista todos os registros filtrando por uma natureza.
	 * @param natureza que se desejar filtrar a seleção.
	 * @return lista de todos os registros referente a <code>natureza</code>
	 * informada.
	 */
	public List<NaturezaCategoriaFluxo> listByNatureza(Natureza natureza) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(NaturezaCategoriaFluxoQuery.QUERY_PARAM_NATUREZA, natureza);
		List<NaturezaCategoriaFluxo> resultList = getNamedResultList
								(NaturezaCategoriaFluxoQuery.LIST_BY_NATUREZA, 
								 parameters);
		return resultList;		
	}
	
}