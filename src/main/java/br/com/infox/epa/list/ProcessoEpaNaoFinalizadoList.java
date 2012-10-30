package br.com.infox.epa.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epa.entity.ProcessoEpa;
import br.com.infox.ibpm.entity.Fluxo;

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
	private static final String DEFAULT_ORDER = "o.porcentagem desc";
	
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

	public Fluxo getFluxo() {
		return fluxo;
	}

	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}

}
