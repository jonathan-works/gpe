package br.com.infox.epp.julgamento.view;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.julgamento.entity.Sala;

@Name(SalaList.NAME)
@Scope(ScopeType.CONVERSATION)
public class SalaList extends EntityList<Sala> {

    private static final String DEFAULT_EJBQL = "select o from Sala o";
    private static final String DEFAULT_ORDER = "nome";

    @Override
    protected void addSearchFields() {
        addSearchField("nome", SearchCriteria.CONTENDO);
        addSearchField("unidadeDecisoraColegiada", SearchCriteria.IGUAL);
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

    private static final long serialVersionUID = 1L;
    public static final String NAME="salaList";
}
