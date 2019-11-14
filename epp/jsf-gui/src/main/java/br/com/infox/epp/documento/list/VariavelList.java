package br.com.infox.epp.documento.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.Variavel;

@Named
@ViewScoped
public class VariavelList extends EntityList<Variavel> {

    private static final long serialVersionUID = 1L;
    
    private static final String TEMPLATE = "/Variavel/variavelTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "Variaveis.xls";

    private static final String DEFAULT_EJBQL = "select o from Variavel o";
    private static final String DEFAULT_ORDER = "variavel";

    @Override
    protected void addSearchFields() {
        addSearchField("variavel", SearchCriteria.CONTENDO);
        addSearchField("valorVariavel", SearchCriteria.CONTENDO);
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
