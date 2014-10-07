package br.com.infox.epp.processo.documento.sigilo.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;

@Name(SigiloDocumentoList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class SigiloDocumentoList extends EntityList<SigiloProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "sigiloDocumentoList";

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
