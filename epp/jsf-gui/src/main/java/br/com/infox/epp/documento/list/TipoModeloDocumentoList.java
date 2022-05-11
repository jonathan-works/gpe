package br.com.infox.epp.documento.list;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.manager.GrupoModeloDocumentoManager;

@Named
@ViewScoped
public class TipoModeloDocumentoList extends EntityList<TipoModeloDocumento> {
    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE = "/TipoModeloDocumento/tipoModeloDocumentoTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "TiposModeloDocumento.xls";

    private static final String DEFAULT_EJBQL = "select o from TipoModeloDocumento o";
    private static final String DEFAULT_ORDER = "grupoModeloDocumento";

    @Inject
    private GrupoModeloDocumentoManager grupoModeloDocumentoManager;

    @Override
    protected void addSearchFields() {
        addSearchField("grupoModeloDocumento", SearchCriteria.IGUAL);
        addSearchField("tipoModeloDocumento", SearchCriteria.CONTENDO);
        addSearchField("abreviacao", SearchCriteria.CONTENDO);
        addSearchField("ativo", SearchCriteria.IGUAL);
        addSearchField("numeracaoAutomatica", SearchCriteria.IGUAL);
    }

    public Integer getIdGrupoModeloDocumento() {
        return getEntity().getGrupoModeloDocumento() == null ? null : getEntity().getGrupoModeloDocumento().getIdGrupoModeloDocumento();
    }

    public void setIdGrupoModeloDocumento(Integer idGrupoModeloDocumento) {
        getEntity().setGrupoModeloDocumento(grupoModeloDocumentoManager.find(idGrupoModeloDocumento));
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
