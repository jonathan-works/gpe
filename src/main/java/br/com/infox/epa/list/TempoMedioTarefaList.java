package br.com.infox.epa.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epa.entity.TempoMedioTarefa;

@Name(TempoMedioTarefaList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class TempoMedioTarefaList extends EntityList<TempoMedioTarefa> {
	public static final String NAME = "tempoMedioTarefaList";
	
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_EJBQL = "select o from TempoMedioTarefa o";
	private static final String DEFAULT_ORDER = "idTarefa";
	
	@Override
	protected void addSearchFields() {
		addSearchField("tempoMedioProcesso", SearchCriteria.igual);
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

}
