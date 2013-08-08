package br.com.infox.epp.list.associative;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.TipoModeloDocumento;

@Name(AssociativeTipoModeloDocumentoList.NAME)
@Scope(ScopeType.PAGE)
public class AssociativeTipoModeloDocumentoList extends
		EntityList<TipoModeloDocumento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "associativeTipoModeloDocumentoList";

	private static final String DEFAULT_EJBQL = "select o from TipoModeloDocumento o where o not in "
			+ "(select v.tipoModeloDocumento from VariavelTipoModelo v where v.variavel = #{variavelHome.definedInstance})";
	private static final String DEFAULT_ORDER = "tipoModeloDocumento";
	
	@Override
	protected void addSearchFields() {
		addSearchField("grupoModeloDocumento", SearchCriteria.IGUAL);
		addSearchField("grupoModeloDocumento.idGrupoModeloDocumento", SearchCriteria.IGUAL);
		addSearchField("tipoModeloDocumento", SearchCriteria.CONTENDO);
		addSearchField("abreviacao", SearchCriteria.CONTENDO);
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
