package br.com.infox.epa.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epa.entity.ProcessoEpa;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.itx.util.EntityUtil;
import br.com.itx.util.StringUtil;

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
	
	private static final String DEFAULT_EJBQL = "select o from ProcessoEpa o " +
												   "where o.dataFim is null";
	private static final String DEFAULT_ORDER = "o.porcentagem asc, o.idProcesso";
	
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
	
	public String rowClasses() {
		List<Object> classes = new ArrayList<Object>();
		for (ProcessoEpa row: list(15)) {
			if (row.getPorcentagem() != null && row.getPorcentagem() > 100) {
				classes.add("red-tr");
			} else if (contemTarefaForaPrazo(row)){
				classes.add("yellow-tr");
			} else {
				classes.add("white-tr");
			}
		}
		return StringUtil.concatList(classes, ",");
	}

}
