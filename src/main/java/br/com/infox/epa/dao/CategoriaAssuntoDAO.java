package br.com.infox.epa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epa.entity.Categoria;
import br.com.infox.epa.entity.CategoriaAssunto;
import br.com.infox.epa.query.CategoriaAssuntoQuery;

/**
 * Classe DAO para a entidade NaturezaCategoriaAssunto
 * @author Daniel
 *
 */
@Name(CategoriaAssuntoDAO.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class CategoriaAssuntoDAO extends GenericDAO {

	public static final String NAME = "categoriaAssuntoDAO";

	public List<CategoriaAssunto> listByCategoria(Categoria categoria) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(CategoriaAssuntoQuery.QUERY_PARAM_CATEGORIA, categoria);
		List<CategoriaAssunto> resultList = getNamedResultList
								(CategoriaAssuntoQuery.LIST_BY_CATEGORIA, 
								 parameters);
		return resultList;		
	}
	
}