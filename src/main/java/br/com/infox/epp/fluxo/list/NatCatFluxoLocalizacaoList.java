package br.com.infox.epp.fluxo.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao;
import br.com.itx.util.ComponentUtil;

@Name(NatCatFluxoLocalizacaoList.NAME)
@Scope(ScopeType.PAGE)
public class NatCatFluxoLocalizacaoList extends EntityList<NatCatFluxoLocalizacao> {
	
	public static final String NAME = "natCatFluxoLocalizacaoList";

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from NatCatFluxoLocalizacao o";
	private static final String DEFAULT_ORDER = "naturezaCategoriaFluxo.natureza";
	
	public static final NatCatFluxoLocalizacaoList instance() {
		return ComponentUtil.getComponent(NAME);
	}
	
	@Override
	protected void addSearchFields() {
		addSearchField("naturezaCategoriaFluxo.natureza", SearchCriteria.IGUAL);
		addSearchField("naturezaCategoriaFluxo.categoria", SearchCriteria.IGUAL);
		addSearchField("naturezaCategoriaFluxo.fluxo", SearchCriteria.IGUAL);
		addSearchField("localizacao", SearchCriteria.IGUAL);
	}

	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		return null;
	}

	@Override
	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}

	@Override
	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}
	
}