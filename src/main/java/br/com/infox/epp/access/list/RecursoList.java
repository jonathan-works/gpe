package br.com.infox.epp.access.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.entity.Recurso;

@Name(RecursoList.NAME)
@Scope(ScopeType.PAGE)
public class RecursoList extends EntityList<Recurso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "recursoList";

    public static final String DEFAULT_EJBQL = "select o from Recurso o";
    public static final String DEFAULT_ORDER = "o.nome";
    
    private static final String TEMPLATE = "/useradmin/RecursoReportTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "Recurso.xls";

    @Override
    protected void addSearchFields() {
        addSearchField("nome", SearchCriteria.CONTENDO);
        addSearchField("identificador", SearchCriteria.CONTENDO);
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

    @Override
    public String getTemplate() {
        return TEMPLATE;
    }
    
    @Override
    public String getDownloadXlsName() {
        return DOWNLOAD_XLS_NAME;
    }
    
}
