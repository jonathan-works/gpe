package br.com.infox.epp.documento.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.documento.entity.Variavel;
import br.com.itx.util.ComponentUtil;

@Name(VariavelList.NAME)
@Scope(ScopeType.PAGE)
public class VariavelList extends EntityList<Variavel> {
	
	public static final String NAME = "variavelList";
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from Variavel o";
	private static final String DEFAULT_ORDER = "variavel";

	@Override
	protected void addSearchFields() {
		addSearchField("variavel", SearchCriteria.CONTENDO);
		addSearchField("valorVariavel", SearchCriteria.CONTENDO);
		addSearchField("ativo", SearchCriteria.IGUAL);
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
	
	public static VariavelList instance() {
		return ComponentUtil.getComponent(VariavelList.NAME);
	}
	
}
