package br.com.infox.epp.processo.list;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.tarefa.entity.ProcessoEpaTarefa;

/**
 * EntityList que consulta todos os processos não finalizados de um determinado fluxo
 * @author tassio
 */
@Name(ProcessoEpaNaoFinalizadoList.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoEpaNaoFinalizadoList extends EntityList<ProcessoEpaTarefa> {

    private static final long serialVersionUID = 1L;
	private static final String DEFAULT_EJBQL = "select o from ProcessoEpaTarefa o " +
												   "inner join o.processoEpa p " +
	                                               "inner join p.naturezaCategoriaFluxo ncf "+
												   "where o.dataFim is null";
	private static final String DEFAULT_ORDER = "p.idProcesso";
	private static final String R1 = "ncf.fluxo = #{processoEpaNaoFinalizadoList.fluxo}";
    public static final String NAME = "processoEpaNaoFinalizadoList";
    
    private static final Map<String,String> CUSTOM_ORDER_MAP;
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
	private boolean updateFluxoList=true;
	
	@In
	private ProcessoEpaManager processoEpaManager;
	
	@Override
	protected void addSearchFields() {
		addSearchField("ncf.fluxo", SearchCriteria.IGUAL, R1);
		addSearchField("processoEpa.situacaoPrazo",SearchCriteria.IGUAL);
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
	    getEntity().setProcessoEpa(new ProcessoEpa());
	    getEntity().getProcessoEpa().setSituacaoPrazo(SituacaoPrazoEnum.PAT);
	}
	
	public Fluxo getFluxo() {
		return fluxo;
	}

	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}
	
	public Double getMediaTempoGasto() {
	    return processoEpaManager.getMediaTempoGasto(fluxo, getEntity().getProcessoEpa().getSituacaoPrazo());
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
	
    public String getNaturezaCategoriaItem(ProcessoEpa processoEpa) {
		NaturezaCategoriaFluxo naturezaCategoriaFluxo = processoEpa.getNaturezaCategoriaFluxo();
		return MessageFormat.format("{0}/{1}/{2}", naturezaCategoriaFluxo.getNatureza().getNatureza(), 
				naturezaCategoriaFluxo.getCategoria().getCategoria(), 
				processoEpa.getItemDoProcesso().getDescricaoItem());
	}
	
	public int getDiasDesdeInicioProcesso(ProcessoEpa processoEpa) {
		return processoEpaManager.getDiasDesdeInicioProcesso(processoEpa);
	}
}
