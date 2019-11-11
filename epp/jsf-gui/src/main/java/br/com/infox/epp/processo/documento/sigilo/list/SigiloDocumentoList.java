package br.com.infox.epp.processo.documento.sigilo.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;

@Named
@ViewScoped
public class SigiloDocumentoList extends EntityList<SigiloProcesso> {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select o from SigiloDocumento o where o.documento = #{sigiloDocumentoList.documento}";
    private static final String DEFAULT_ORDER = "dataInclusao desc";

    private Documento documento;

    @Override
    protected void addSearchFields() {
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

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }
}
