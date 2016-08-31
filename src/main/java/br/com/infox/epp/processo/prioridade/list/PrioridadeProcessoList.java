package br.com.infox.epp.processo.prioridade.list;

import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso;
import br.com.infox.epp.processo.prioridade.manager.PrioridadeProcessoManager;

@Name(PrioridadeProcessoList.NAME)
@Scope(ScopeType.PAGE)
public class PrioridadeProcessoList extends EntityList<PrioridadeProcesso> {

    public static final String NAME = "prioridadeProcessoList";

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select o from PrioridadeProcesso o";
    private static final String DEFAULT_ORDER = "o.peso desc";
    
    @In
    private PrioridadeProcessoManager prioridadeProcessoManager;

    @Override
    protected void addSearchFields() {
        addSearchField("peso", SearchCriteria.IGUAL);
        addSearchField("descricaoPrioridade", SearchCriteria.CONTENDO);
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

    public List<PrioridadeProcesso> listAtivos() {
        return prioridadeProcessoManager.listPrioridadesAtivas();
    }
}
