package br.com.infox.epp.processo.list;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.MetadadoProcessoType;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;

@Scope(ScopeType.CONVERSATION)
@Name(ProcessoEpaNaoFinalizadoList.NAME)
public class ProcessoEpaNaoFinalizadoList extends EntityList<ProcessoTarefa> {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_EJBQL = "select o from ProcessoTarefa o "
            + "inner join o.processo p "
            + "inner join p.naturezaCategoriaFluxo ncf "
            + "where o.dataFim is null";
    
    private static final String DEFAULT_ORDER = "p.idProcesso";
    private static final String R1 = "ncf.fluxo = #{processoEpaNaoFinalizadoList.fluxo}";
    private static final String R2 = "p.situacaoPrazo = #{processoEpaNaoFinalizadoList.entity.processo.situacaoPrazo}";
    public static final String NAME = "processoEpaNaoFinalizadoList";

    private static final Map<String, String> CUSTOM_ORDER_MAP;
    
    static {
        CUSTOM_ORDER_MAP = new HashMap<>();
        CUSTOM_ORDER_MAP.put("fluxo", "ncf.fluxo");
        CUSTOM_ORDER_MAP.put("prioridadeProcesso", "p.prioridadeProcesso");
        CUSTOM_ORDER_MAP.put("dataChegadaTarefa", "o.dataInicio");
        CUSTOM_ORDER_MAP.put("tempoGastoTarefa", "o.tempoGasto");
        CUSTOM_ORDER_MAP.put("dataInicio", "p.dataInicio");
    }

    private Fluxo fluxo;
    private List<Fluxo> fluxoList;
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
        List<Fluxo> fluxos = getFluxoList();
        if (!fluxos.isEmpty()) {
            fluxo = fluxos.get(0);
        }
        super.newInstance();
        getEntity().setProcesso(new Processo());
        getEntity().getProcesso().setSituacaoPrazo(SituacaoPrazoEnum.PAT);
    }

    public Fluxo getFluxo() {
        return fluxo;
    }

    public void setFluxo(Fluxo fluxo) {
        this.fluxo = fluxo;
    }

    public long getMediaTempoGastoDesdeInicioProcesso() {
        long media = 0;
        StringBuilder hql = new StringBuilder("select p.dataInicio from Processo p ");
        hql.append("inner join p.naturezaCategoriaFluxo ncf ");
        hql.append("where p.dataFim is null ");
        if (getFluxo() != null) {
            hql.append("and ncf.fluxo = :fluxo ");
        }
        if (getEntity().getProcesso().getSituacaoPrazo() != null) {
            hql.append("and p.situacaoPrazo = :situacaoPrazo ");
        }
        TypedQuery<Date> query = getEntityManager().createQuery(hql.toString(), Date.class);
        if (getFluxo() != null) {
            query.setParameter("fluxo", getFluxo());
        }
        if (getEntity().getProcesso().getSituacaoPrazo() != null) {
            query.setParameter("situacaoPrazo", getEntity().getProcesso().getSituacaoPrazo());
        }

        LocalDate now = LocalDate.now();
        List<Date> result = query.getResultList();
        for (Date dataInicio : result) {
            LocalDate data = LocalDate.fromDateFields(dataInicio);
            media += Days.daysBetween(data, now).getDays();
        }

        return !result.isEmpty() ? media / result.size() : 0;
    }

    public List<SituacaoPrazoEnum> getTiposSituacaoPrazo() {
        return Arrays.asList(SituacaoPrazoEnum.values());
    }

    public List<Fluxo> getFluxoList() {
        if (updateFluxoList) {
            fluxoList = getEntityManager().createQuery("select o from Fluxo o order by o.fluxo", Fluxo.class).getResultList();
        }
        return fluxoList;
    }

    public void setFluxoList(List<Fluxo> fluxoList) {
        this.fluxoList = fluxoList;
    }

    public String getNaturezaCategoriaItem(Processo processo) {
        NaturezaCategoriaFluxo naturezaCategoriaFluxo = processo.getNaturezaCategoriaFluxo();
        MetadadoProcesso metadadoProcesso = processo.getMetadado(MetadadoProcessoType.ITEM_DO_PROCESSO);
        if (metadadoProcesso == null) {
            return MessageFormat.format("{0}/{1}", naturezaCategoriaFluxo.getNatureza().getNatureza(), naturezaCategoriaFluxo.getCategoria().getCategoria());
        } else {
        	Item item = metadadoProcesso.getValue();
            return MessageFormat.format("{0}/{1}/{2}", naturezaCategoriaFluxo.getNatureza().getNatureza(), naturezaCategoriaFluxo.getCategoria().getCategoria(), item.getDescricaoItem());
        }
    }

    public int getDiasDesdeInicioProcesso(Processo processo) {
        return processoTarefaManager.getDiasDesdeInicioProcesso(processo);
    }
}
