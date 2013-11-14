package br.com.infox.epp.endereco.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.endereco.entity.Endereco;

@Name(EnderecoList.NAME)
@Scope(ScopeType.PAGE)
public class EnderecoList extends EntityList<Endereco> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "enderecoList";
	public static final String DEFAULT_EJBQL = "select o from Endereco o";
	public static final String DEFAULT_ORDER = "o.nomeLogradouro";
	
	@Override
	protected void addSearchFields() {
		addSearchField("usuario", SearchCriteria.CONTENDO, "o.usuario = #{enderecoList.entity.usuario}");
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
