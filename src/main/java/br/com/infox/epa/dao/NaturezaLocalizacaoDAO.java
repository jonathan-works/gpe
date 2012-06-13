package br.com.infox.epa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epa.entity.Natureza;
import br.com.infox.epa.entity.NaturezaLocalizacao;
import br.com.infox.epa.query.NaturezaLocalizacaoQuery;

/**
 * Classe DAO para a entidade NaturezaCategoriaAssunto
 * @author Daniel
 *
 */
@Name(NaturezaLocalizacaoDAO.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class NaturezaLocalizacaoDAO extends GenericDAO {

	public static final String NAME = "naturezaLocalizacaoDAO";

	/**
	 * Lista todos os registros filtrando por uma natureza.
	 * @param natureza que se desejar filtrar a seleção.
	 * @return lista de todos os registros referente a <code>natureza</code>
	 * informada.
	 */
	public List<NaturezaLocalizacao> listByNatureza(Natureza natureza) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(NaturezaLocalizacaoQuery.QUERY_PARAM_NATUREZA, natureza);
		List<NaturezaLocalizacao> resultList = getNamedResultList
								(NaturezaLocalizacaoQuery.LIST_BY_NATUREZA, 
								 parameters);
		return resultList;		
	}
	
}