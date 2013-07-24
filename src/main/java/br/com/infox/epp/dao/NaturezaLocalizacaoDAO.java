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
import br.com.infox.epp.entity.NaturezaLocalizacao;
import br.com.infox.epp.query.NaturezaLocalizacaoQuery;

/**
 * Classe DAO para a entidade NaturezaLocalizacao
 * @author Daniel
 *
 */
@Name(NaturezaLocalizacaoDAO.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class NaturezaLocalizacaoDAO extends GenericDAO {

	private static final long serialVersionUID = 6988083662252576007L;
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