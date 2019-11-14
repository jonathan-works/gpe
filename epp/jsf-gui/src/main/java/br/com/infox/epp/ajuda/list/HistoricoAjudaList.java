package br.com.infox.epp.ajuda.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.ajuda.entity.HistoricoAjuda;

@Name(HistoricoAjudaList.NAME)
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class HistoricoAjudaList extends EntityList<HistoricoAjuda> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "historicoAjudaList";

    private static final String DEFAULT_EJBQL = "select o from HistoricoAjuda o";
    private static final String DEFAULT_ORDER = "dataRegistro desc";

    private static final String R1 = "pagina.url = #{ajudaCrudAction.viewId}";

    protected void addSearchFields() {
        addSearchField("pagina.url", SearchCriteria.IGUAL, R1);
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }

    protected String getDefaultEjbql() {
        return DEFAULT_EJBQL;
    }

    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }
    
    public boolean isExibGrid(){
    	Long qtRegistros = getResultCount();
    	return qtRegistros != null && qtRegistros > 0;
    }
}
