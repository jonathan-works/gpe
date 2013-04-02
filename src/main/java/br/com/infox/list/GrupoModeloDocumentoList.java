package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.GrupoModeloDocumento;
import br.com.itx.util.ComponentUtil;

@Name(GrupoModeloDocumentoList.NAME)
@BypassInterceptors
@Scope(ScopeType.CONVERSATION)
public class GrupoModeloDocumentoList extends EntityList<GrupoModeloDocumento> {

	public static final String NAME = "grupoModeloDocumentoList";
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from GrupoModeloDocumento o";
	private static final String DEFAULT_ORDER = "grupoModeloDocumento";
	
	public static final GrupoModeloDocumentoList instance() {
		return ComponentUtil.getComponent(NAME);
	}
	
	protected void addSearchFields() {
		addSearchField("grupoModeloDocumento", SearchCriteria.contendo);
		addSearchField("ativo", SearchCriteria.igual);
	}

	protected Map<String, String> getCustomColumnsOrder() {
		return null;
	}

	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}

	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}
}