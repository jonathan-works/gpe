package br.com.infox.epp.access.list;

import java.util.Map;

import static br.com.infox.core.list.SearchCriteria.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.access.entity.Perfil;

@Name(PerfilList.NAME)
@Scope(ScopeType.PAGE)
public class PerfilList extends EntityList<Perfil> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "perfilList";
    
    public static final String TEMPLATE = "/Perfil/PerfilTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "Perfis.xls";
    
    private static final String DEFAULT_EJBQL = "select o from Perfil o";
    private static final String DEFAULT_ORDER = "descricao";

    @Override
    protected void addSearchFields() {
        addSearchField("descricao", CONTENDO);
        addSearchField("localizacao", IGUAL);
        addSearchField("papel", IGUAL);
        addSearchField("ativo", IGUAL);
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
