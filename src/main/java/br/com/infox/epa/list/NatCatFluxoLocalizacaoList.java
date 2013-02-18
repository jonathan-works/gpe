package br.com.infox.epa.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epa.entity.NatCatFluxoLocalizacao;
import br.com.infox.epa.entity.NaturezaCategoriaFluxo;
import br.com.itx.util.ComponentUtil;

@Name(NatCatFluxoLocalizacaoList.NAME)
@BypassInterceptors
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
		addSearchField("naturezaCategoriaFluxo.natureza", SearchCriteria.igual);
		addSearchField("naturezaCategoriaFluxo.categoria", SearchCriteria.igual);
		addSearchField("naturezaCategoriaFluxo.fluxo", SearchCriteria.igual);
		addSearchField("localizacao", SearchCriteria.igual);
		addSearchField("heranca", SearchCriteria.igual);
	}

	@Override
	public void newInstance() {
		super.newInstance();
		entity.setNaturezaCategoriaFluxo(new NaturezaCategoriaFluxo()) ;
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