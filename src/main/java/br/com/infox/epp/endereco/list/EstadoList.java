package br.com.infox.epp.endereco.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.endereco.entity.Estado;

@Name(EstadoList.NAME)
@Scope(ScopeType.PAGE)
public class EstadoList extends EntityList<Estado> {
	
	public static final String NAME = "estadoList";
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from Estado o";
	private static final String DEFAULT_ORDER = "estado";

	@Override
	protected void addSearchFields() {
		addSearchField("estado", SearchCriteria.IGUAL);

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
