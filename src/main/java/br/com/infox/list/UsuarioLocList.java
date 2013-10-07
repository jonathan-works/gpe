package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.UsuarioLocalizacao;

@Name(UsuarioLocList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class UsuarioLocList extends EntityList<UsuarioLocalizacao> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "usuarioLocList";
	public static final String DEFAULT_EJBQL = "select o from UsuarioLocalizacao o";
	public static final String DEFAULT_ORDER = "o.localizacao";
	
	@Override
	protected void addSearchFields() {
		addSearchField("usuario", SearchCriteria.CONTENDO, "o.usuario = #{usuarioLocList.entity.usuario}");
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
