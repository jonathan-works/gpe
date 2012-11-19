package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.*;
import br.com.infox.ibpm.entity.Pessoa;

@Name(PessoaList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class PessoaList extends EntityList<Pessoa> {
	
	private static final long serialVersionUID = 1L;

	public static final String NAME = "pessoaList";
	
	private static final String DEFAULT_EJBQL = "select o from Pessoa o";
	private static final String DEFAULT_ORDER = "nome";

	@Override
	protected void addSearchFields() {
		addSearchField("nome", SearchCriteria.contendo);
		addSearchField("tipoPessoa", SearchCriteria.contendo);
		addSearchField("ativo", SearchCriteria.igual);		
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
