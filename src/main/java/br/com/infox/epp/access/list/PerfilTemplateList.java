package br.com.infox.epp.access.list;

import static br.com.infox.constants.WarningConstants.UNCHECKED;
import static br.com.infox.core.list.SearchCriteria.CONTENDO;
import static br.com.infox.core.list.SearchCriteria.IGUAL;

import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.tree.TreeHandler;
import br.com.infox.epp.access.component.tree.EstruturaLocalizacoesPerfilTreeHandler;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
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
    
    @Override
    public void newInstance() {
        super.newInstance();
        clearTrees();
    }
    
    @SuppressWarnings(UNCHECKED)
    private void clearTrees() {
        ((TreeHandler<Papel>) Component.getInstance(PapelTreeHandler.NAME)).clearTree();
        ((TreeHandler<Localizacao>) Component.getInstance(EstruturaLocalizacoesPerfilTreeHandler.NAME)).clearTree();
    }

}
