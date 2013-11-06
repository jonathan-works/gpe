package br.com.infox.epp.documento.list.associative;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.documento.entity.Variavel;

@Name(AssociativeVariavelList.NAME)
@Scope(ScopeType.PAGE)
public class AssociativeVariavelList extends EntityList<Variavel> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "associativeVariavelList";
	
	private static final String DEFAULT_EJBQL = "select o from Variavel o where o not in " +
													"(select v.variavel from VariavelTipoModelo v where " +
														"v.tipoModeloDocumento = #{tipoModeloDocumentoHome.definedInstance})";
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

}
