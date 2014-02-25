package br.com.infox.epp.fluxo.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;

@Name(NaturezaCategoriaFluxoList.NAME)
@Scope(ScopeType.CONVERSATION)
public class NaturezaCategoriaFluxoList extends EntityList<NaturezaCategoriaFluxo> {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_EJBQL = "select o from NaturezaCategoriaFluxo o";
    private static final String DEFAULT_ORDER = "fluxo";
    public static final String NAME = "naturezaCategoriaFluxoList";

    @Override
    protected void addSearchFields() {
        addSearchField("natureza", SearchCriteria.IGUAL);
        addSearchField("categoria", SearchCriteria.IGUAL);
        addSearchField("fluxo", SearchCriteria.IGUAL);
    }

    @Override
    protected String getDefaultEjbql() {
        return NaturezaCategoriaFluxoList.DEFAULT_EJBQL;
    }

    @Override
    protected String getDefaultOrder() {
        return NaturezaCategoriaFluxoList.DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }

}
