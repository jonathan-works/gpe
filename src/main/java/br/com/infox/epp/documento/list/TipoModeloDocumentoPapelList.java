package br.com.infox.epp.documento.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.documento.entity.TipoModeloDocumentoPapel;

@Name(TipoModeloDocumentoPapelList.NAME)
@Scope(ScopeType.PAGE)
public class TipoModeloDocumentoPapelList extends
		EntityList<TipoModeloDocumentoPapel> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tipoModeloDocumentoPapelList";
	
	public static final String DEFAULT_EJBQL = "select o from TipoModeloDocumentoPapel o";
	public static final String DEFAULT_ORDER = "o.papel.nome";
	

	@Override
	protected void addSearchFields() {
		addSearchField("tipoModeloDocumento", SearchCriteria.IGUAL);

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
