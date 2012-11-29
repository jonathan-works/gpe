package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.PessoaJuridica;
import br.com.itx.util.ComponentUtil;

@Name(PessoaJuridicaList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class PessoaJuridicaList extends EntityList<PessoaJuridica> {

	public static final String NAME = "pessoaJuridicaList";

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from PessoaJuridica o";
	private static final String DEFAULT_ORDER = "nome";
	
	@Override
	protected void addSearchFields() {
		addSearchField("cnpj", SearchCriteria.igual);
		addSearchField("nome", SearchCriteria.contendo);
		addSearchField("razaoSocial", SearchCriteria.contendo);
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
	
	public static PessoaJuridicaList instance(){
		return ComponentUtil.getComponent(PessoaJuridicaList.NAME);
	}
	
	
}
