package br.com.infox.epp.processo.sigilo.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;

@Name(SigiloProcessoList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class SigiloProcessoList extends EntityList<SigiloProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "sigiloProcessoList";

    private static final String DEFAULT_EJBQL = "select o from SigiloProcesso o where o.processo = #{sigiloProcessoList.processo}";
    private static final String DEFAULT_ORDER = "dataInclusao desc";

    private Processo processo;

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

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

}
