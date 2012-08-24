package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.LocalizacaoFisica;

@Name(LocalizacaoFisicaList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class LocalizacaoFisicaList extends EntityList<LocalizacaoFisica> {

	public static final String NAME = "localizacaoFisicaList";

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from LocalizacaoFisica o";
	private static final String DEFAULT_ORDER = "descricaoSala";
	
	@Override
	protected void addSearchFields() {
		addSearchField("nrPrateleira", SearchCriteria.igual);
		addSearchField("nrCaixa", SearchCriteria.igual);
		addSearchField("descricaoSala", SearchCriteria.contendo);
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
