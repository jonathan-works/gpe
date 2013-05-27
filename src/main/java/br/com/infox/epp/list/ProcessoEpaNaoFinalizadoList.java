package br.com.infox.epp.list;

import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.entity.ProcessoEpa;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.itx.util.EntityUtil;

/**
 * EntityList que consulta todos os processos não finalizados de um determinado fluxo
 * @author tassio
 */
@Name(ProcessoEpaNaoFinalizadoList.NAME)
@Scope(ScopeType.PAGE)
public class ProcessoEpaNaoFinalizadoList extends EntityList<ProcessoEpa> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoEpaNaoFinalizadoList";

	private Fluxo fluxo;
	private String fluxoName;
	
	private static final String DEFAULT_EJBQL = "select o from ProcessoEpa o " +
												   "where o.dataFim is null";
	private static final String DEFAULT_ORDER = "case when o.situacaoPrazo = 'PAT' then 0" +
												   "    when o.situacaoPrazo = 'TAT' then 1" +
												   "    else 2 end asc, o.idProcesso";
	
	private static final String R1 = "o.naturezaCategoriaFluxo.fluxo = #{processoEpaNaoFinalizadoList.fluxo}";
	

	@Override
	protected void addSearchFields() {
		addSearchField("fluxo", SearchCriteria.igual, R1);
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
	public void setOrderedColumn(String order) {
		setOrder(order);
	}

	public Fluxo getFluxo() {
		if (fluxoName != null && fluxo == null) {
			Query q = EntityUtil.createQuery("select o from Fluxo o where o.fluxo = :name");
			q.setParameter("name", fluxoName);
			fluxo = EntityUtil.getSingleResult(q);
		}
		return fluxo;
	}

	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}
	
	public boolean contemTarefaForaPrazo(ProcessoEpa processoEpa) {
		Query query = EntityUtil.createQuery("select count(o) from ProcessoEpaTarefa o " +
											 "where o.processoEpa = :processoEpa " +
											 "  and o.porcentagem > 100");
		query.setParameter("processoEpa", processoEpa);
		return (Long) query.getSingleResult() > 0;
	}
	
	public Double getMediaTempoGasto() {
		String hql = "select avg(pEpa.tempoGasto) " +
				"from ProcessoEpa pEpa " +
				"where pEpa.naturezaCategoriaFluxo.fluxo = :fluxo " +
				"and pEpa.dataFim is null and pEpa.contabilizar=true " +
				"group by pEpa.naturezaCategoriaFluxo.fluxo";
		Query query = EntityUtil.createQuery(hql);
		query.setParameter("fluxo", fluxo);
		return EntityUtil.getSingleResult(query);
	}
	
	public String getFluxoName() {
		return fluxoName;
	}

	public void setFluxoName(String fluxoName) {
		this.fluxoName = fluxoName;
	}

}
