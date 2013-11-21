package br.com.infox.epp.documento.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.documento.entity.ItemTipoDocumento;

@Name(ItemTipoDocumentoList.NAME)
@Scope(ScopeType.PAGE)
public class ItemTipoDocumentoList extends EntityList<ItemTipoDocumento> {
	
	public static final String NAME = "itemTipoDocumentoList";

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from ItemTipoDocumento o";
	private static final String DEFAULT_ORDER = "o";
	
	protected void addSearchFields() {
		addSearchField("localizacao", SearchCriteria.IGUAL);
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