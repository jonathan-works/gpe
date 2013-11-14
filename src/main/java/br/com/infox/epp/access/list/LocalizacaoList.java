package br.com.infox.epp.access.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.access.entity.Localizacao;

@Name(LocalizacaoList.NAME)
@Scope(ScopeType.PAGE)
public class LocalizacaoList extends EntityList<Localizacao> {

	public static final String NAME = "localizacaoList";
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from Localizacao o";
	private static final String DEFAULT_ORDER = "o.caminhoCompleto";
	
	@Override
	protected void addSearchFields() {
		addSearchField("localizacao", SearchCriteria.CONTENDO);
		addSearchField("localizacaoPai", SearchCriteria.IGUAL);
		addSearchField("ativo", SearchCriteria.IGUAL);
		addSearchField("estrutura", SearchCriteria.IGUAL);
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
