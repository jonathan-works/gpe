package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.Localizacao;

@Name(LocalizacaoList.NAME)
@Scope(ScopeType.PAGE)
public class LocalizacaoList extends EntityList<Localizacao> {

	public static final String NAME = "localizacaoList";
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from Localizacao o";
	private static final String DEFAULT_ORDER = "o.caminhoCompleto";
	
	@Override
	protected void addSearchFields() {
		addSearchField("localizacao", SearchCriteria.contendo);
		addSearchField("localizacaoPai", SearchCriteria.igual);
		addSearchField("ativo", SearchCriteria.igual);
		addSearchField("estrutura", SearchCriteria.igual);
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
		// TODO Auto-generated method stub
		return null;
	}

}
