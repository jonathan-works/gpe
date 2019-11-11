package br.com.infox.epp.fluxo.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;

@Named
@ViewScoped
public class NaturezaCategoriaFluxoList extends EntityList<NaturezaCategoriaFluxo> {
    
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_EJBQL = "select o from NaturezaCategoriaFluxo o";
    private static final String DEFAULT_ORDER = "fluxo";

    @Override
    protected void addSearchFields() {
        addSearchField("natureza", SearchCriteria.IGUAL);
        addSearchField("categoria", SearchCriteria.IGUAL);
        addSearchField("fluxo", SearchCriteria.IGUAL);
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
