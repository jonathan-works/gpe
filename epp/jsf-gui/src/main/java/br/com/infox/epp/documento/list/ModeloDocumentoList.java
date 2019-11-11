package br.com.infox.epp.documento.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ModeloDocumento;

@Named
@ViewScoped
public class ModeloDocumentoList extends EntityList<ModeloDocumento> {

    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE = "/ModeloDocumento/modeloDocumentoTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "ModelosDocumento.xls";

    private static final String DEFAULT_EJBQL = "select o from ModeloDocumento o";
    private static final String DEFAULT_ORDER = "tituloModeloDocumento";
    private static final String R1 = "exists (from TipoModeloDocumentoPapel tmdp"
            + " where tmdp.tipoModeloDocumento = o.tipoModeloDocumento"
            + " and tmdp.papel = #{usuarioLogadoPerfilAtual.getPerfilTemplate().papel})";

    protected void addSearchFields() {
        addSearchField("ativo", SearchCriteria.IGUAL);
        addSearchField("tipoModeloDocumento", SearchCriteria.IGUAL);
        addSearchField("tituloModeloDocumento", SearchCriteria.CONTENDO);
        addSearchField("codigo", SearchCriteria.CONTENDO);
        addSearchField("validaPapel", SearchCriteria.IGUAL, R1);
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
    public String getTemplate() {
        return TEMPLATE;
    }

    @Override
    public String getDownloadXlsName() {
        return DOWNLOAD_XLS_NAME;
    }
}
