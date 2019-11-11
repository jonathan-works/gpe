package br.com.infox.epp.fluxo.list;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Categoria;

@Named
@ViewScoped
public class CategoriaList extends EntityList<Categoria> {

    private static final long serialVersionUID = 1L;

    public static final String TEMPLATE = "/Categoria/CategoriaTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "Categoria.xls";

    private static final String DEFAULT_EJBQL = "select o from Categoria o";
    private static final String DEFAULT_ORDER = "categoria";

    @Override
    protected void addSearchFields() {
        addSearchField("categoria", SearchCriteria.CONTENDO);
        addSearchField("ativo", SearchCriteria.IGUAL);
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
