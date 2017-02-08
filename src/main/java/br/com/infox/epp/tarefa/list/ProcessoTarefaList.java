package br.com.infox.epp.tarefa.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.primefaces.model.chart.MeterGaugeChartModel;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;

@Name(ProcessoTarefaList.NAME)
public class ProcessoTarefaList extends EntityList<ProcessoTarefa> {

    private static final int PORCENTAGEM = 100;
    private static final int LIMITE_PADRAO = 15;
    private static final long serialVersionUID = 1L;
    public static final String NAME = "processoTarefaList";

    private static final String DEFAULT_EJBQL = "select o from ProcessoTarefa o ";
    private static final String DEFAULT_ORDER = "o.dataInicio";

    @In
    private ProcessoTarefaManager processoTarefaManager;

    @Override
    protected void addSearchFields() {
        addSearchField("processo", SearchCriteria.IGUAL);
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
        for (ProcessoTarefa row : list(LIMITE_PADRAO)) {
            if (row.getPorcentagem() != null
                    && row.getPorcentagem() > PORCENTAGEM) {
                classes.add("red-back");
            } else {
                classes.add("white-back");
            }
        }
        return StringUtil.concatList(classes, ",");
    }
    
    public MeterGaugeChartModel getMeterTempoGastoDesdeInicioProcesso(){
        MeterGaugeChartModel gauge = new MeterGaugeChartModel();
        gauge.setValue(getEntity().getProcesso().getTempoGasto());
        gauge.setMin(0);
        Fluxo fluxo = getEntity().getProcesso().getNaturezaCategoriaFluxo().getFluxo();
        gauge.setMax(fluxo.getQtPrazo());
        gauge.setGaugeLabel(fluxo.getFluxo());
        gauge.setGaugeLabelPosition("top");
        gauge.setShowTickLabels(true);
        return gauge;
    }
    
}
