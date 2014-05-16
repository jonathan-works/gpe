package br.com.infox.epp.tarefa.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.tarefa.entity.ProcessoEpaTarefa;

/**
 * EntityList que consulta as tarefas de um determinado processo
 * 
 * @author tassio
 * 
 */
@Name(ProcessoEpaTarefaList.NAME)
public class ProcessoEpaTarefaList extends EntityList<ProcessoEpaTarefa> {

    private static final int PORCENTAGEM = 100;
    private static final int LIMITE_PADRAO = 15;
    private static final long serialVersionUID = 1L;
    public static final String NAME = "processoEpaTarefaList";

    private static final String DEFAULT_EJBQL = "select o from ProcessoEpaTarefa o";
    private static final String DEFAULT_ORDER = "o.dataInicio";

    @In
    private ProcessoEpaManager processoEpaManager;

    @Override
    protected void addSearchFields() {
        addSearchField("processoEpa", SearchCriteria.IGUAL);
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

    public String rowClasses() {
        List<Object> classes = new ArrayList<Object>();
        for (ProcessoEpaTarefa row : list(LIMITE_PADRAO)) {
            if (row.getPorcentagem() != null
                    && row.getPorcentagem() > PORCENTAGEM) {
                classes.add("red-back");
            } else {
                classes.add("white-back");
            }
        }
        return StringUtil.concatList(classes, ",");
    }

    public int getDiasDesdeInicioProcesso() {
        return processoEpaManager.getDiasDesdeInicioProcesso(getEntity().getProcessoEpa());
    }
}
