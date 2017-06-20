package br.com.infox.epp.access.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.cdi.ViewScoped;

@Named
@ViewScoped
public class EstruturaList extends EntityList<Estrutura> {

    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_EJBQL = "select o from Estrutura o";
    public static final String DEFAULT_ORDER = "nome";
    
    private static final String TEMPLATE = "/Estrutura/estruturaTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "Estruturas.xls";

    @Override
    protected void addSearchFields() {
        addSearchField("nome", SearchCriteria.CONTENDO);
        addSearchField("ativo", SearchCriteria.IGUAL);
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
