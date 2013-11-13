package br.com.infox.epp.documento.list.associated;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.documento.entity.VariavelTipoModelo;

@Name(AssociatedTipoModeloVariavelList.NAME)
@Scope(ScopeType.PAGE)
public class AssociatedTipoModeloVariavelList extends
		EntityList<VariavelTipoModelo> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "associatedTipoModeloVariavelList";
	
	private static final String DEFAULT_EJBQL = "select o from VariavelTipoModelo o";
	private static final String DEFAULT_ORDER = "tipoModeloDocumento";

	@Override
	protected void addSearchFields() {
		addSearchField("tipoModeloDocumento", SearchCriteria.IGUAL);
		addSearchField("tipoModeloDocumento.grupoModeloDocumento", SearchCriteria.IGUAL);
		addSearchField("variavel", SearchCriteria.IGUAL);
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
