package br.com.infox.epp.fluxo.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.fluxo.entity.CategoriaItem;

@Name(CategoriaItemList.NAME)
@Scope(ScopeType.PAGE)
public class CategoriaItemList extends EntityList<CategoriaItem> {
    
    private static final long serialVersionUID = 1L;
    public static final String NAME = "categoriaItemList";
    
    private static final String DEFAULT_EJBQL = "select o from CategoriaItem o";
    private static final String DEFAULT_ORDER = "item.caminhoCompleto";

    @Override
    protected void addSearchFields() {
        addSearchField("categoria", SearchCriteria.IGUAL);

    }

    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_EJBQL;
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }

}
