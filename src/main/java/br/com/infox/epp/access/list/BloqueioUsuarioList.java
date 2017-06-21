package br.com.infox.epp.access.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.entity.BloqueioUsuario;
import br.com.infox.epp.cdi.ViewScoped;

@Named
@ViewScoped
public class BloqueioUsuarioList extends EntityList<BloqueioUsuario> {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select o from BloqueioUsuario o";
    private static final String DEFAULT_ORDER = "dataBloqueio desc";

    @Override
    protected void addSearchFields() {
        addSearchField("usuario", SearchCriteria.IGUAL);
        addSearchField("dataBloqueio", SearchCriteria.DATA_IGUAL);
        addSearchField("dataPrevisaoDesbloqueio", SearchCriteria.DATA_IGUAL);
        addSearchField("dataDesbloqueio", SearchCriteria.DATA_IGUAL);
        addSearchField("motivoBloqueio", SearchCriteria.CONTENDO);
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

}
