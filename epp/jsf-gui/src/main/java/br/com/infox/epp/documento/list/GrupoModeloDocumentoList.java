package br.com.infox.epp.documento.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;

@Named
@ViewScoped
public class GrupoModeloDocumentoList extends EntityList<GrupoModeloDocumento> {

    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE = "/GrupoModeloDocumento/grupoModeloDocumentoTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "GruposModeloDocumento.xls";

    private static final String DEFAULT_EJBQL = "select o from GrupoModeloDocumento o";
    private static final String DEFAULT_ORDER = "grupoModeloDocumento";

    protected void addSearchFields() {
        addSearchField("grupoModeloDocumento", SearchCriteria.CONTENDO);
        addSearchField("ativo", SearchCriteria.IGUAL);
    }

    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }

    protected String getDefaultEjbql() {
        return DEFAULT_EJBQL;
    }

    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
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
