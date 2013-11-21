package br.com.infox.epp.access.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.entity.UsuarioLogin;

@Name(UsuarioLoginList.NAME)
@Scope(ScopeType.PAGE)
public class UsuarioLoginList extends EntityList<UsuarioLogin> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "usuarioLoginList";
	
	private static final String DEFAULT_EJBQL = "select o from UsuarioLogin o";
	private static final String DEFAULT_ORDER = "nome";

	@Override
	protected void addSearchFields() {
		addSearchField("nome", SearchCriteria.CONTENDO);
		addSearchField("bloqueio",SearchCriteria.IGUAL);
		addSearchField("provisorio",SearchCriteria.IGUAL);
		addSearchField("ativo", SearchCriteria.IGUAL);
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
