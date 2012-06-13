package br.com.infox.epa.dao;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epa.query.CategoriaQuery;

/**
 * Classe DAO para a entidade Categoria
 * @author Daniel
 *
 */
@Name(CategoriaDAO.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class CategoriaDAO extends GenericDAO {

	public static final String NAME = "categoriaDAO";

	public List<Object[]> listProcessoByCategoria() {
		List<Object[]> resultList = getNamedResultList(
				CategoriaQuery.LIST_PROCESSO_EPA_BY_CATEGORIA, null);
		return resultList;
	}
	
}