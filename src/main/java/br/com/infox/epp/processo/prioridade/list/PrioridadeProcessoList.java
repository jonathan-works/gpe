package br.com.infox.epp.processo.prioridade.list;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso;
import br.com.infox.epp.processo.prioridade.manager.PrioridadeProcessoManager;

@Named
@ViewScoped
public class PrioridadeProcessoList extends EntityList<PrioridadeProcesso> {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select o from PrioridadeProcesso o";
    private static final String DEFAULT_ORDER = "o.peso desc";
    
    @Inject
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
