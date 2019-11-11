package br.com.infox.epp.fluxo.dao;

import static br.com.infox.epp.fluxo.query.CategoriaItemQuery.COUNT_BY_CATEGORIA_ITEM;
import static br.com.infox.epp.fluxo.query.CategoriaItemQuery.LIST_BY_CATEGORIA;
import static br.com.infox.epp.fluxo.query.CategoriaItemQuery.QUERY_PARAM_CATEGORIA;
import static br.com.infox.epp.fluxo.query.CategoriaItemQuery.QUERY_PARAM_ITEM;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.CategoriaItem;
import br.com.infox.epp.fluxo.entity.Item;

@Stateless
@AutoCreate
@Name(CategoriaItemDAO.NAME)
public class CategoriaItemDAO extends DAO<CategoriaItem> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "categoriaItemDAO";

    public List<CategoriaItem> listByCategoria(Categoria categoria) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(QUERY_PARAM_CATEGORIA, categoria);
        return getNamedResultList(LIST_BY_CATEGORIA, parameters);
    }

    public Long countByCategoriaItem(Categoria categoria, Item item) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(QUERY_PARAM_CATEGORIA, categoria);
        parameters.put(QUERY_PARAM_ITEM, item);
        return getNamedSingleResult(COUNT_BY_CATEGORIA_ITEM, parameters);
    }

}
