package br.com.infox.epp.fluxo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.CategoriaItem;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.query.CategoriaItemQuery;

@Name(CategoriaItemDAO.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class CategoriaItemDAO extends GenericDAO {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "categoriaItemDAO";

    public List<CategoriaItem> listByCategoria(Categoria categoria) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        parameters.put(CategoriaItemQuery.QUERY_PARAM_CATEGORIA, categoria);
        
        return getNamedResultList(CategoriaItemQuery.LIST_BY_CATEGORIA,
                parameters);
    }

    public Long countByCategoriaItem(Categoria categoria, Item item) {
        Map<String, Object> parameters = new HashMap<String, Object>();

        parameters.put(CategoriaItemQuery.QUERY_PARAM_CATEGORIA, categoria);
        parameters.put(CategoriaItemQuery.QUERY_PARAM_ITEM, item);

        return getNamedSingleResult(CategoriaItemQuery.COUNT_BY_CATEGORIA_ITEM,
                parameters);
    }

}