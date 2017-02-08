package br.com.infox.epp.processo.list;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import org.apache.commons.lang3.ObjectUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.primefaces.model.chart.MeterGaugeChartModel;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.util.CollectionUtil;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;

@Scope(ScopeType.CONVERSATION)
@Name(ProcessoEpaNaoFinalizadoList.NAME)
public class ProcessoEpaNaoFinalizadoList extends EntityList<ProcessoTarefa> {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_EJBQL = "select distinct p from ProcessoTarefa o "
            + "inner join o.processo p "
            + "inner join p.naturezaCategoriaFluxo ncf "
            + "where p.dataFim is null";
    
    private static final String DEFAULT_ORDER = "p.idProcesso";
    private static final String R1 = "ncf.fluxo = #{processoEpaNaoFinalizadoList.fluxo}";
    private static final String R2 = "p.situacaoPrazo = #{processoEpaNaoFinalizadoList.situacaoPrazo}";
    public static final String NAME = "processoEpaNaoFinalizadoList";

    private static final Map<String, String> CUSTOM_ORDER_MAP;
    
    static {
        Map<String,String> map = new HashMap<>();
        map.put("fluxo", "ncf.fluxo");
        map.put("prioridadeProcesso", "p.prioridadeProcesso");
        map.put("dataChegadaTarefa", "o.dataInicio");
        map.put("tempoGastoTarefa", "o.tempoGasto");
        map.put("dataInicio", "p.dataInicio");
        CUSTOM_ORDER_MAP = Collections.unmodifiableMap(map);
    }

    private Fluxo fluxo;
    private List<Fluxo> fluxoList;
    private SituacaoPrazoEnum situacaoPrazo;
    private boolean updateFluxoList = true;

    @In
    private ProcessoTarefaManager processoTarefaManager;

    @Override
    protected void addSearchFields() {
        addSearchField("ncf.fluxo", SearchCriteria.IGUAL, R1);
        addSearchField("p.situacaoPrazo", SearchCriteria.IGUAL, R2);
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
        return CUSTOM_ORDER_MAP;
    }

    @Override
    public void newInstance() {
        this.situacaoPrazo = null;
        super.newInstance();
    }

    public Fluxo getFluxo() {
        return ObjectUtils.defaultIfNull(this.fluxo, CollectionUtil.firstOrNull(getFluxoList()));
    }

    public void setFluxo(Fluxo fluxo) {
        this.fluxo = fluxo;
    }

    public MeterGaugeChartModel getMeterMediaTempoGastoDesdeInicioProcesso(){
    	MeterGaugeChartModel gauge = new MeterGaugeChartModel();
        gauge.setGaugeLabel(InfoxMessages.getInstance().get("bam.medidorProcSel"));
        gauge.setGaugeLabelPosition("top");
        gauge.setMin(0);
        gauge.setValue(getMediaTempoGastoDesdeInicioProcesso());
        gauge.setMax(getFluxo()!= null ? getFluxo().getQtPrazo(): 0);
        gauge.setShowTickLabels(true);
        return gauge;
    }
    
    public double getMediaTempoGastoDesdeInicioProcesso() {
    	if(getFluxo() != null) {
            StringBuilder hql = new StringBuilder("select avg(p.tempoGasto) from Processo p ");
            hql.append("inner join p.naturezaCategoriaFluxo ncf ");
            hql.append("where p.dataFim is null ");
            if (getFluxo() != null) {
                hql.append("and ncf.fluxo = :fluxo ");
            }
            if (getSituacaoPrazo() != null) {
                hql.append("and p.situacaoPrazo = :situacaoPrazo ");
            }
            TypedQuery<Double> query = getEntityManager().createQuery(hql.toString(), Double.class);
            if (getFluxo() != null) {
                query.setParameter("fluxo", getFluxo());
            }
            if (getSituacaoPrazo() != null) {
                query.setParameter("situacaoPrazo", getSituacaoPrazo());
            }
    
            return CollectionUtil.firstOrNull(query.getResultList());
        } else return 0.0;
    }

    public List<SituacaoPrazoEnum> getTiposSituacaoPrazo() {
        return Arrays.asList(SituacaoPrazoEnum.values());
    }

    public List<Fluxo> getFluxoList() {
        if (updateFluxoList) {
            fluxoList = getEntityManager().createQuery("select o from Fluxo o where exists (select 1 from Processo p inner join p.naturezaCategoriaFluxo ncf inner join ncf.fluxo fluxo where fluxo=o and p.dataFim is null) order by o.fluxo", Fluxo.class).getResultList();
        }
        return fluxoList;
    }

    public void setFluxoList(List<Fluxo> fluxoList) {
        this.fluxoList = fluxoList;
    }

    public String getNaturezaCategoriaItem(Processo processo) {
        NaturezaCategoriaFluxo naturezaCategoriaFluxo = processo.getNaturezaCategoriaFluxo();
        MetadadoProcesso metadadoProcesso = processo.getMetadado(EppMetadadoProvider.ITEM_DO_PROCESSO);
        if (metadadoProcesso == null) {
            return MessageFormat.format("{0}/{1}", naturezaCategoriaFluxo.getNatureza().getNatureza(), naturezaCategoriaFluxo.getCategoria().getCategoria());
        } else {
        	Item item = metadadoProcesso.getValue();
            return MessageFormat.format("{0}/{1}/{2}", naturezaCategoriaFluxo.getNatureza().getNatureza(), naturezaCategoriaFluxo.getCategoria().getCategoria(), item.getDescricaoItem());
        }
    }

    public SituacaoPrazoEnum getSituacaoPrazo() {
        return situacaoPrazo;
    }

    public void setSituacaoPrazo(SituacaoPrazoEnum situacaoPrazo) {
        this.situacaoPrazo = situacaoPrazo;
    }
}
