package br.com.infox.epp.processo.documento.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.entity.ProcessoEpa;

@Name(DocumentoAnexoList.NAME)
@Scope(ScopeType.CONVERSATION)
public class DocumentoAnexoList extends EntityList<ProcessoDocumento> {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_EJBQL = "select pd.* from tb_processo_documento pd "
            + "inner join tb_processo p on (p.id_processo = pd.id_processo) where "
            + "pd.id_processo = #{documentoAnexoList.processo} and "
            + "not exists (select 1 from jbpm_variableinstance v where "
            + "v.longvalue_ = pd.id_processo_documento and "
            + "v.taskinstance_ in (select t.id_ from jbpm_taskinstance t where t.procinst_ = p.id_jbpm)"
            + ")";
    private static final String DEFAULT_ORDER = "pd.dt_inclusao";

    public static final String NAME = "documentoAnexoList";

    private ProcessoEpa processo;

    public DocumentoAnexoList() {
        setNativeQuery(true);
        setResultClass(ProcessoDocumento.class);
    }

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

    public ProcessoEpa getProcesso() {
        return processo;
    }

    public void setProcesso(ProcessoEpa processo) {
        this.processo = processo;
    }
}
