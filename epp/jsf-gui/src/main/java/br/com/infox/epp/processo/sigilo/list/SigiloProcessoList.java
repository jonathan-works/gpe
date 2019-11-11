package br.com.infox.epp.processo.sigilo.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;

@Named
@ViewScoped
public class SigiloProcessoList extends EntityList<SigiloProcesso> {

    private static final long serialVersionUID = 1L;

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
