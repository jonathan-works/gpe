package br.com.infox.epp.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.entity.CalendarioEventos;
import br.com.itx.util.ComponentUtil;

@Name(CalendarioEventosList.NAME)
@BypassInterceptors
@Scope(ScopeType.CONVERSATION)
public class CalendarioEventosList extends EntityList<CalendarioEventos> {
	
	public static final String NAME = "calendarioEventosList";

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from CalendarioEventos o";
	private static final String DEFAULT_ORDER = "descricaoEvento";
	 
	@Override
	protected void addSearchFields() {
		addSearchField("descricaoEvento", SearchCriteria.contendo);
		addSearchField("localizacao", SearchCriteria.igual);
		addSearchField("dia", SearchCriteria.igual);
		addSearchField("mes", SearchCriteria.igual);
		addSearchField("ano", SearchCriteria.igual);
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
	
	public static CalendarioEventosList instance() {
		return ComponentUtil.getComponent(CalendarioEventosList.NAME);
	}
	
}