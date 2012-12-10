package br.com.infox.epa.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.TipoModeloDocumento;
import br.com.itx.util.ComponentUtil;


@Name(TipoModeloDocumentoList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class TipoModeloDocumentoList extends EntityList<TipoModeloDocumento> {
	public static final String NAME = "tipoModeloDocumentoList";
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_EJBQL = "select o from TipoModeloDocumento o";
	private static final String DEFAULT_ORDER = "grupoModeloDocumento";
	
	public static final TipoModeloDocumentoList instance() {
		return ComponentUtil.getComponent(NAME);
	}
	
	@Override
	protected void addSearchFields() {
		addSearchField("grupoModeloDocumento", SearchCriteria.igual);
		addSearchField("tipoModeloDocumento", SearchCriteria.contendo);
		addSearchField("abreviacao", SearchCriteria.contendo);
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
