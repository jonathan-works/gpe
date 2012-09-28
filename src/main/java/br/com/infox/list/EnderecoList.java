package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.Endereco;

@Name(EnderecoList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class EnderecoList extends EntityList<Endereco> {

	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "enderecoList";
	public static final String DEFAULT_EJBQL = "select o from Endereco o";
	public static final String DEFAULT_ORDER = "o.nomeLogradouro";
	
	@Override
	protected void addSearchFields() {
		addSearchField("usuario", SearchCriteria.contendo, "o.usuario = #{enderecoList.entity.usuario}");
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
