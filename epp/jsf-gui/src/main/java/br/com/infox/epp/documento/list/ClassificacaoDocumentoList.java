package br.com.infox.epp.documento.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;

@Named
@ViewScoped
public class ClassificacaoDocumentoList extends EntityList<ClassificacaoDocumento> {

    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE = "/ClassificacaoDocumento/tipoProcessoDocumentoTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "ClassificacaoDocumento.xls";

    private static final String DEFAULT_EJBQL = "select o from ClassificacaoDocumento o";
    private static final String DEFAULT_ORDER = "descricao";

    @Override
    protected void addSearchFields() {
        addSearchField("codigoDocumento", SearchCriteria.CONTENDO);
        addSearchField("descricao", SearchCriteria.CONTENDO);
        addSearchField("inTipoDocumento", SearchCriteria.IGUAL);
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
