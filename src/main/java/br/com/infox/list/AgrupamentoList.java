package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.Agrupamento;
import br.com.itx.util.ComponentUtil;

@Name(AgrupamentoList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class AgrupamentoList extends EntityList<Agrupamento> {
	
	public static final String NAME = "agrupamentoList";

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from Agrupamento o";
	private static final String DEFAULT_ORDER = "agrupamento";
	
	public static AgrupamentoList instance()	{
		return ComponentUtil.getComponent(AgrupamentoList.NAME);
	}
	
	protected void addSearchFields() {
		addSearchField("agrupamento", SearchCriteria.contendo);
		addSearchField("ativo", SearchCriteria.igual);
	}

	protected Map<String, String> getCustomColumnsOrder() {
		return null;
	}

	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}

	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}
	
}