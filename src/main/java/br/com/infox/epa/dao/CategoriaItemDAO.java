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
import br.com.infox.epa.entity.CategoriaItem;
import br.com.infox.epa.query.CategoriaItemQuery;

@Name(CategoriaItemDAO.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class CategoriaItemDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "categoriaItemDAO";

	public List<CategoriaItem> listByCategoria(Categoria categoria) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(CategoriaItemQuery.QUERY_PARAM_CATEGORIA, categoria);
		List<CategoriaItem> resultList = getNamedResultList
								(CategoriaItemQuery.LIST_BY_CATEGORIA, 
								 parameters);
		return resultList;		
	}
	
}