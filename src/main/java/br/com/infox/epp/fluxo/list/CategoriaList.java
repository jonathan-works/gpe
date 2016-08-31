package br.com.infox.epp.fluxo.list;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.seam.util.ComponentUtil;

@Name(CategoriaList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class CategoriaList extends EntityList<Categoria> {

    public static final String NAME = "categoriaList";

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

    public static CategoriaList instance() {
        return ComponentUtil.getComponent(CategoriaList.NAME);
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
