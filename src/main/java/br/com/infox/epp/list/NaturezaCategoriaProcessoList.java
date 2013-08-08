package br.com.infox.epp.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.entity.NaturezaCategoriaFluxo;

@Name(NaturezaCategoriaProcessoList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class NaturezaCategoriaProcessoList extends EntityList<NaturezaCategoriaFluxo> {
	public static final String NAME = "natCatProcessoList";
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select ncf from NatCatFluxoLocalizacao o " +
												   "inner join o.naturezaCategoriaFluxo ncf " +
												   "inner join ncf.fluxo.fluxoPapelList papelList";
	private static final String DEFAULT_ORDER = "ncf.natureza";
	private static final String R1 = "o.localizacao = #{usuarioLogadoLocalizacaoAtual.getLocalizacao()}";
	private static final String R2 = "papelList.papel = #{usuarioLogadoLocalizacaoAtual.getPapel()}";

	@Override
	protected void addSearchFields() {
		addSearchField("localizacao", SearchCriteria.IGUAL, R1);
		addSearchField("papel", SearchCriteria.IGUAL, R2);
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
