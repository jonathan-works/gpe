package br.com.infox.epp.tce.prestacaocontas.modelo.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.ModeloPrestacaoContas;

@Name(ModeloPrestacaoContasList.NAME)
@Scope(ScopeType.PAGE)
public class ModeloPrestacaoContasList extends EntityList<ModeloPrestacaoContas> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "modeloPrestacaoContasList";

    private static final String DEFAULT_EJBQL = "select o from ModeloPrestacaoContas o";
    private static final String DEFAULT_ORDER = "nome";
    private static final String TEMPLATE = "/ModeloPrestacaoContas/ModeloPrestacaoContasTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "ModeloPrestacaoContas.xls";
    
    @Override
    protected void addSearchFields() {
        addSearchField("tipoPrestacaoContas", SearchCriteria.IGUAL);
        addSearchField("esfera", SearchCriteria.IGUAL);
        addSearchField("grupoPrestacaoContas", SearchCriteria.IGUAL);
        addSearchField("anoExercicio", SearchCriteria.IGUAL);
        addSearchField("nome", SearchCriteria.CONTENDO);
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
    public String getDownloadXlsName() {
        return DOWNLOAD_XLS_NAME;
    }
    
    @Override
    public String getTemplate() {
        return TEMPLATE;
    }
}
