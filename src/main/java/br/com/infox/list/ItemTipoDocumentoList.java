package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.ItemTipoDocumento;

@Name(ItemTipoDocumentoList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class ItemTipoDocumentoList extends EntityList<ItemTipoDocumento> {
	
	public static final String NAME = "itemTipoDocumentoList";

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from ItemTipoDocumento o";
	private static final String DEFAULT_ORDER = "o";
	
	private static final String R1 = "o.localizacao = #{localizacaoHome.instance}";
	 
	protected void addSearchFields() {
		addSearchField("localizacao", SearchCriteria.IGUAL, R1);
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