package br.com.infox.epp.processo.list;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

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
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;

@Scope(ScopeType.CONVERSATION)
@Name(ProcessoEpaNaoFinalizadoList.NAME)
public class ProcessoEpaNaoFinalizadoList extends EntityList<Processo> {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_EJBQL = "select o from Processo o "
            + "inner join o.naturezaCategoriaFluxo ncf "
            + "where o.dataFim is null";

    private static final String DEFAULT_ORDER = "o.idProcesso";
    private static final String R1 = "ncf.fluxo = #{processoEpaNaoFinalizadoList.fluxo}";
    private static final String R2 = "o.situacaoPrazo = #{processoEpaNaoFinalizadoList.situacaoPrazo}";
    public static final String NAME = "processoEpaNaoFinalizadoList";

    private static final Map<String, String> CUSTOM_ORDER_MAP;

    static {
        Map<String,String> map = new HashMap<>();
        map.put("fluxo", "ncf.fluxo");
        map.put("prioridadeProcesso", "o.prioridadeProcesso");
        map.put("dataInicio", "o.dataInicio");
        CUSTOM_ORDER_MAP = Collections.unmodifiableMap(map);
    }

    private Fluxo fluxo;
    private List<Fluxo> fluxoList;
    private SituacaoPrazoEnum situacaoPrazo;
    private boolean updateFluxoList = true;
    @Getter @Setter
    private Date dataInicio;
    @Getter @Setter
    private Date dataFim;

    @In
    private ProcessoTarefaManager processoTarefaManager;

    @Override
    protected void addSearchFields() {
        addSearchField("ncf.fluxo", SearchCriteria.IGUAL, R1);
        addSearchField("o.situacaoPrazo", SearchCriteria.IGUAL, R2);
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
        this.fluxo = null;
        this.dataFim = null;
        this.dataInicio = null;
        super.newInstance();
    }

    public Fluxo getFluxo() {
        return this.fluxo;
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
        Double result = null;
        if(getFluxo() != null) {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Double> cq = cb.createQuery(Double.class);
            Root<Processo> processo = cq.from(Processo.class);
            From<?, NaturezaCategoriaFluxo> natCatFluxo = processo.join(Processo_.naturezaCategoriaFluxo, JoinType.INNER);

            Predicate restrictions = processo.get(Processo_.dataFim).isNull();
            if (getFluxo() != null) {
                restrictions = cb.and(restrictions,cb.equal(natCatFluxo.get(NaturezaCategoriaFluxo_.fluxo), getFluxo()));
            }
            if (getSituacaoPrazo() != null) {
                restrictions = cb.and(restrictions,cb.equal(processo.get(Processo_.situacaoPrazo), getSituacaoPrazo()));
            }
            cq = cq.select(cb.avg(processo.get(Processo_.tempoGasto))).where(restrictions);

            TypedQuery<Double> query = getEntityManager().createQuery(cq);

            result=CollectionUtil.firstOrNull(query.getResultList());
        }
        return ObjectUtils.defaultIfNull(result, 0.0);
    }

    public List<SituacaoPrazoEnum> getTiposSituacaoPrazo() {
        return Arrays.asList(SituacaoPrazoEnum.values());
    }

    public List<Fluxo> getFluxoList() {
        if (updateFluxoList) {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Fluxo> cq = cb.createQuery(Fluxo.class);
            Root<Fluxo> fluxo = cq.from(Fluxo.class);

            Subquery<Integer> sq = cq.subquery(Integer.class);
            Root<Processo> processo = sq.from(Processo.class);
            From<?, NaturezaCategoriaFluxo> natCatFluxo = processo.join(Processo_.naturezaCategoriaFluxo, JoinType.INNER);

            sq = sq.select(
                cb.literal(1)
            ).where(
                cb.equal(natCatFluxo.get(NaturezaCategoriaFluxo_.fluxo), fluxo),
                processo.get(Processo_.dataFim).isNull()
            );

            cq=cq.select(fluxo).where(cb.exists(sq)).orderBy(cb.asc(fluxo.get(Fluxo_.fluxo)));
            fluxoList = getEntityManager().createQuery(cq).getResultList();
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

    @Override
    public List<Processo> getResultList() {
        setEjbql(getEjbqlRestrictedWithDataTarefa());
        return super.getResultList();
    }

    private String getEjbqlRestrictedWithDataTarefa() {
        if (dataInicio == null
                && dataFim == null) {
            return getDefaultEjbql();
        }

        StringBuilder sb = new StringBuilder(getDefaultEjbql());

        if (dataInicio != null
                && dataFim != null) {
            sb.append(" and exists ("
                    + "select pt.idProcessoTarefa "
                    + "from ProcessoTarefa pt "
                    + "where pt.processo = o "
                    + "      and pt.dataInicio >= #{processoEpaNaoFinalizadoList.dataInicio} "
                    + "      and pt.dataInicio <= #{processoEpaNaoFinalizadoList.dataFim}"
                    + ") ");
        }

        return sb.toString();
    }


}
