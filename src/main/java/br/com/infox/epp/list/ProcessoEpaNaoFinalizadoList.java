package br.com.infox.epp.list;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.entity.ProcessoEpa;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.type.SituacaoPrazoEnum;
import br.com.itx.util.EntityUtil;

/**
 * EntityList que consulta todos os processos não finalizados de um determinado fluxo
 * @author tassio
 */
@Name(ProcessoEpaNaoFinalizadoList.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoEpaNaoFinalizadoList extends EntityList<ProcessoEpa> {

    private static final long serialVersionUID = 1L;
	private static final String DEFAULT_EJBQL = "select o from ProcessoEpa o " +
	                                               "inner join o.naturezaCategoriaFluxo.fluxo f "+
												   "where o.dataFim is null";
	private static final String DEFAULT_ORDER = "o.idProcesso";
	private static final String R1 = "o.naturezaCategoriaFluxo.fluxo = #{processoEpaNaoFinalizadoList.fluxo}";
    public static final String NAME = "processoEpaNaoFinalizadoList";
    
	private Fluxo fluxo;
	private List<Fluxo> fluxoList;
	private boolean updateFluxoList=true;
	

	@Override
	protected void addSearchFields() {
		addSearchField("naturezaCategoriaFluxo.fluxo", SearchCriteria.IGUAL, R1);
		addSearchField("situacaoPrazo",SearchCriteria.IGUAL);
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
	    fluxo = getFluxoList().get(0);
	    super.newInstance();
	    getEntity().setSituacaoPrazo(SituacaoPrazoEnum.PAT);
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
                .setParameter("situacao", getEntity().getSituacaoPrazo());
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
	
}
