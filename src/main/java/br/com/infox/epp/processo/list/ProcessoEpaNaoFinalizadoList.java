package br.com.infox.epp.processo.list;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.entity.ProcessoEpaTarefa;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.type.SituacaoPrazoEnum;
import br.com.itx.util.EntityUtil;

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
	                                               "inner join p.naturezaCategoriaFluxo.fluxo f "+
												   "where o.dataFim is null";
	private static final String DEFAULT_ORDER = "p.idProcesso";
	private static final String R1 = "p.naturezaCategoriaFluxo.fluxo = #{processoEpaNaoFinalizadoList.fluxo}";
    public static final String NAME = "processoEpaNaoFinalizadoList";
    
	private Fluxo fluxo;
	private List<Fluxo> fluxoList;
	private boolean updateFluxoList=true;
	
	@In
	private ProcessoEpaManager processoEpaManager;
	
	@Override
	protected void addSearchFields() {
		addSearchField("processoEpa.naturezaCategoriaFluxo.fluxo", SearchCriteria.IGUAL, R1);
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
		return null;
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
	
	public Integer getMaxTempoGasto() {
	    final String hql = "select max(pEpa.tempoGasto) " +
	    		"from ProcessoEpa pEpa " +
	    		"inner join pEpa.naturezaCategoriaFluxo ncf " +
	    		"where ncf.fluxo=:fluxo " +
	    		"and pEpa.dataFim is null " +
	    		"group by ncf.fluxo";
	    Query query = EntityUtil.createQuery(hql)
	            .setParameter("fluxo", fluxo);
	    Integer result = EntityUtil.getSingleResult(query);
	    return result == null ? 0 : result;
	}
	
	public Double getMediaTempoGasto() {
	    final String hql = "select avg(pEpa.tempoGasto) " +
	    		"from ProcessoEpa pEpa " +
	    		"inner join pEpa.naturezaCategoriaFluxo ncf " +
	    		"where ncf.fluxo=:fluxo " +
	    		"and pEpa.dataFim is null " +
	    		"and pEpa.contabilizar=true " +
	    		"and pEpa.situacaoPrazo=:situacao " +
	    		"group by ncf.fluxo";
	    Query query = EntityUtil.createQuery(hql)
                .setParameter("fluxo", fluxo)
                .setParameter("situacao", getEntity().getProcessoEpa().getSituacaoPrazo());
	    return EntityUtil.getSingleResult(query);
	}
	
	public boolean contemTarefaForaPrazo(ProcessoEpa processoEpa) {
		Query query = EntityUtil.createQuery("select count(o) from ProcessoEpaTarefa o " +
											 "where o.processoEpa = :processoEpa " +
											 "  and o.porcentagem > 100");
		query.setParameter("processoEpa", processoEpa);
		return (Long) query.getSingleResult() > 0;
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
