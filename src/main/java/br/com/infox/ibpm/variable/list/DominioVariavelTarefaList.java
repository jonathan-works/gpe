package br.com.infox.ibpm.variable.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;

@Name(DominioVariavelTarefaList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class DominioVariavelTarefaList extends EntityList<DominioVariavelTarefa> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "dominioVariavelTarefaList";
	private static final String DEFAULT_EJBQL = "select o from DominioVariavelTarefa o";
	private static final String DEFAULT_ORDER = "nome";

	@Override
	protected void addSearchFields() {
		addSearchField("nome", SearchCriteria.CONTENDO);
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
