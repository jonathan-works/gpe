package br.com.infox.epp.documento.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.seam.util.ComponentUtil;

@Name(TipoProcessoDocumentoList.NAME)
@Scope(ScopeType.PAGE)
public class TipoProcessoDocumentoList extends EntityList<TipoProcessoDocumento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "tipoProcessoDocumentoList";

    private static final String TEMPLATE = "/ClassificacaoDocumento/tipoProcessoDocumentoTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "ClassificacaoDocumento.xls";

    private static final String DEFAULT_EJBQL = "select o from TipoProcessoDocumento o";
    private static final String DEFAULT_ORDER = "tipoProcessoDocumento";

    @Override
    protected void addSearchFields() {
        addSearchField("codigoDocumento", SearchCriteria.CONTENDO);
        addSearchField("tipoProcessoDocumento", SearchCriteria.CONTENDO);
        addSearchField("inTipoDocumento", SearchCriteria.IGUAL);
        addSearchField("visibilidade", SearchCriteria.IGUAL);
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

    public static TipoProcessoDocumentoList instance() {
        return ComponentUtil.getComponent(TipoProcessoDocumentoList.NAME);
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
