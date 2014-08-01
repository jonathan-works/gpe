package br.com.infox.epp.access.list;

import static br.com.infox.core.list.SearchCriteria.*;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.access.entity.PerfilTemplate;

@Name(PerfilTemplateList.NAME)
@Scope(ScopeType.EVENT)
public class PerfilTemplateList extends EntityList<PerfilTemplate> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "perfilTemplateList";
    
    private static final String DEFAULT_EJBQL = "select o from PerfilTemplate o ";
    private static final String DEFAULT_ORDER = "o.descricao";
    
    public static final String TEMPLATE = "/Perfil/PerfilTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "Perfis.xls";
    
    @Override
    protected void addSearchFields() {
        addSearchField("descricao", IGUAL);
        addSearchField("localizacao", IGUAL);
        addSearchField("papel", IGUAL);
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
