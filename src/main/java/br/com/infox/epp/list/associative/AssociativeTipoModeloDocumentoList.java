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
	
//	public static final String R1 = "grupoModeloDocumento = #{grupoModeloDocumentoSuggest.instance}";
//	public static final String R2 = "grupoModeloDocumento.idGrupoModeloDocumento = #{grupoModeloDocumentoHome.id}";
//	public static final String R3 = "lower(tipoModeloDocumento) like concat('%',lower(#{tipoModeloDocumentoSearch.tipoModeloDocumento}),'%')";
//	public static final String R4 = "lower(abreviacao) like concat(lower(#{tipoModeloDocumentoSearch.abreviacao}),'%')";
//	public static final String R5 = "ativo = #{tipoModeloDocumentoSearch.ativo}";

	@Override
	protected void addSearchFields() {
		addSearchField("grupoModeloDocumento", SearchCriteria.igual);
		addSearchField("grupoModeloDocumento.idGrupoModeloDocumento", SearchCriteria.igual);
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
