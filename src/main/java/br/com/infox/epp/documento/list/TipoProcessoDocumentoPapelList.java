package br.com.infox.epp.documento.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.documento.entity.TipoProcessoDocumentoPapel;

@Name(TipoProcessoDocumentoPapelList.NAME)
@Scope(ScopeType.PAGE)
public class TipoProcessoDocumentoPapelList extends
		EntityList<TipoProcessoDocumentoPapel> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tipoProcessoDocumentoPapelList";
	
	public static final String DEFAULT_EJBQL = "select o from TipoProcessoDocumentoPapel o";
	public static final String DEFAULT_ORDER = "papel";
	
	
	@Override
	protected void addSearchFields() {
		addSearchField("tipoProcessoDocumento", SearchCriteria.IGUAL);
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
