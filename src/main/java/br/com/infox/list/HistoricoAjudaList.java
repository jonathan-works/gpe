package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.help.HistoricoAjuda;

@Name(HistoricoAjudaList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class HistoricoAjudaList extends EntityList<HistoricoAjuda> {
	
	public static final String NAME = "historicoAjudaList";

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from HistoricoAjuda o";
	private static final String DEFAULT_ORDER = "dataRegistro desc";
	
	private static final String R1 = "pagina.url = #{ajudaHome.viewId}";

	protected void addSearchFields() {
		addSearchField("pagina.url", SearchCriteria.igual, R1);
	}

	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		// TODO Auto-generated method stub
		return null;
	}

	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}

	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}

}