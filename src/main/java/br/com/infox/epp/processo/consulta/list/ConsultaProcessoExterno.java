package br.com.infox.epp.processo.consulta.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.sigilo.manager.SigiloProcessoPermissaoManager;

@Name(ConsultaProcessoExterno.NAME)
@Scope(ScopeType.PAGE)
public class ConsultaProcessoExterno extends EntityList<Processo> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "consultaProcessoExterno";

    private static final String DEFAULT_EJBQL = "select o from Processo o where "
            + SigiloProcessoPermissaoManager.getPermissaoConditionFragment();
    private static final String DEFAULT_ORDER = "dataInicio ASC";

    private boolean exibirTable = false;

    @Override
    protected void addSearchFields() {
        addSearchField("numeroProcesso", SearchCriteria.IGUAL);
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

    public void exibirTable() {
        exibirTable = true;
    }

    public void esconderTable() {
        newInstance();
        exibirTable = false;
    }

    public boolean isExibirTable() {
        return this.exibirTable;
    }

}
