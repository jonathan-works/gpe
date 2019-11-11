package br.com.infox.epp.fluxo.list;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Natureza;

@Named
@ViewScoped
public class NaturezaList extends EntityList<Natureza> {

    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE = "/Natureza/NaturezaTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "Naturezas.xls";

    private static final String DEFAULT_EJBQL = "select o from Natureza o";
    private static final String DEFAULT_ORDER = "natureza";

    @Override
    protected void addSearchFields() {
        addSearchField("natureza", SearchCriteria.CONTENDO);
        addSearchField("ativo", SearchCriteria.IGUAL);
        addSearchField("hasPartes", SearchCriteria.IGUAL);
        addSearchField("codigo", SearchCriteria.CONTENDO);
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("fluxo", "fluxo.fluxo");
        return map;
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
    public String getTemplate() {
        return TEMPLATE;
    }

    @Override
    public String getDownloadXlsName() {
        return DOWNLOAD_XLS_NAME;
    }

}
