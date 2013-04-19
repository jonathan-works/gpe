package br.com.infox.epa.list;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epa.entity.Natureza;
import br.com.itx.util.ComponentUtil;

@Name(NaturezaList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class NaturezaList extends EntityList<Natureza> {
	
	public static final String NAME = "naturezaList";

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from Natureza o";
	private static final String DEFAULT_ORDER = "natureza";

	public static final NaturezaList instance() {
		return ComponentUtil.getComponent(NaturezaList.NAME);
	}
	
	@Override
	protected void addSearchFields() {
		addSearchField("natureza", SearchCriteria.contendo);
		addSearchField("ativo", SearchCriteria.igual);
		addSearchField("hasPartes", SearchCriteria.igual);
	}

	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("fluxo", "fluxo.fluxo");
		return map;
	}

	@Override
	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}

	@Override
	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}
	
}