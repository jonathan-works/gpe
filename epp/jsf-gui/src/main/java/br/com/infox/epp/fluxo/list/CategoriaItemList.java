package br.com.infox.epp.fluxo.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.CategoriaItem;

@Named
@ViewScoped
public class CategoriaItemList extends EntityList<CategoriaItem> {
    
    private static final long serialVersionUID = 1L;
    
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
