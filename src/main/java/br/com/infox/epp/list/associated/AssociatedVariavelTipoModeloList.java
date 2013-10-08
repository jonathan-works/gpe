package br.com.infox.epp.list.associated;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.ibpm.entity.VariavelTipoModelo;

@Name(AssociatedVariavelTipoModeloList.NAME)
@Scope(ScopeType.PAGE)
public class AssociatedVariavelTipoModeloList extends
		EntityList<VariavelTipoModelo> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "associatedVariavelTipoModeloList";
	
	private static final String DEFAULT_EJBQL = "select o from VariavelTipoModelo o where o.tipoModeloDocumento = #{tipoModeloDocumentoHome.definedInstance}";
	private static final String DEFAULT_ORDER = "variavel";

	@Override
	protected void addSearchFields() {
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
