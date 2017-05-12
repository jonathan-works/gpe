package br.com.infox.epp.access.list;

import static br.com.infox.core.list.SearchCriteria.CONTENDO;
import static br.com.infox.core.list.SearchCriteria.IGUAL;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.access.component.tree.EstruturaLocalizacoesPerfilTreeHandler;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.config.BeanManager;

@Named
@ViewScoped
public class PerfilTemplateList extends EntityList<PerfilTemplate> {

    private static final long serialVersionUID = 1L;
    
    private static final String DEFAULT_EJBQL = "select o from PerfilTemplate o ";
    private static final String DEFAULT_ORDER = "o.descricao";
    
    public static final String TEMPLATE = "/Perfil/PerfilTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "Perfis.xls";
    
    @Override
    protected void addSearchFields() {
        addSearchField("codigo", CONTENDO);
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
    
    private void clearTrees() {
        BeanManager.INSTANCE.getReference(PapelTreeHandler.class).clearTree();
        BeanManager.INSTANCE.getReference(EstruturaLocalizacoesPerfilTreeHandler.class).clearTree();
    }

}
