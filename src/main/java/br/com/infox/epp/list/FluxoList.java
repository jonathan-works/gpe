package br.com.infox.epp.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.itx.util.ComponentUtil;

@Name(FluxoList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class FluxoList extends EntityList<Fluxo> {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_EJBQL = "select o from Fluxo o";
    private static final String DEFAULT_ORDER = "fluxo";
    
    public static final String NAME = "fluxoList";

    @Override
    protected void addSearchFields() {
        addSearchField("codFluxo", SearchCriteria.IGUAL);
        addSearchField("fluxo", SearchCriteria.IGUAL);
        addSearchField("dataInicioPublicacao", SearchCriteria.DATA_IGUAL);
        addSearchField("dataFimPublicacao", SearchCriteria.DATA_IGUAL);
        addSearchField("publicado", SearchCriteria.IGUAL);
        addSearchField("ativo", SearchCriteria.IGUAL);
    }

    @Override
    protected String getDefaultEjbql() {
        return FluxoList.DEFAULT_EJBQL;
    }

    @Override
    protected String getDefaultOrder() {
        return FluxoList.DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }

    public static final FluxoList instance() {
        return ComponentUtil.getComponent(FluxoList.NAME);
    }
    
}
