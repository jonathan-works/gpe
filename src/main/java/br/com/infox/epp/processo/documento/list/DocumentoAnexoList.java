package br.com.infox.epp.processo.documento.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.system.Configuration;
import br.com.infox.epp.system.Database.DatabaseType;

@Name(DocumentoAnexoList.NAME)
@Scope(ScopeType.CONVERSATION)
public class DocumentoAnexoList extends EntityList<Documento> {
	
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_EJBQL = "select pd.* from tb_documento pd "
    		+ "inner join tb_pasta pa on (pa.id_pasta = pd.id_pasta) "
            + "inner join tb_processo p on (p.id_processo = pa.id_processo) "
            + "inner join tb_documento_bin bin on (bin.id_documento_bin = pd.id_documento_bin) "
            + "where "
            + "pd.id_processo = #{documentoAnexoList.processo} and "
            + "not exists (select 1 from jbpm_variableinstance v where "
            + "v.longvalue_ = pd.id_documento and "
            + "v.taskinstance_ in (select t.id_ from jbpm_taskinstance t where t.procinst_ = p.id_jbpm)"
            + ") "; 
    private static final String DEFAULT_ORDER = "pd.dt_inclusao";

    public static final String NAME = "documentoAnexoList";

    private Processo processo;
    
    public DocumentoAnexoList() {
        setNativeQuery(true);
        setResultClass(Documento.class);
    }

    @Override
    protected void addSearchFields() {
    }

    @Override
    protected String getDefaultEjbql() {
    	DatabaseType databaseType = Configuration.getInstance().getDatabase().getDatabaseType();
    	String queryAppend = "";
    	if (databaseType.equals(DatabaseType.PostgreSQL)){
    		queryAppend = " and pd.in_excluido = false and bin.in_minuta = false ";
    	} else if (databaseType.equals(DatabaseType.SQLServer) || databaseType.equals(DatabaseType.Oracle)) {
    		queryAppend = " and pd.in_excluido = 0 and bin.in_minuta = 0 ";
    	}
        return DEFAULT_EJBQL + queryAppend;
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

}
