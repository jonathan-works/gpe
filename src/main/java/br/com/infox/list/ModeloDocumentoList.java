package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.ModeloDocumento;

@Name(ModeloDocumentoList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class ModeloDocumentoList extends EntityList<ModeloDocumento> {

	public static final String NAME = "modeloDocumentoList";
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from ModeloDocumento o";
	private static final String DEFAULT_ORDER = "tituloModeloDocumento";
	
	protected void addSearchFields() {
		addSearchField("ativo", SearchCriteria.igual);
		addSearchField("tipoModeloDocumento", SearchCriteria.igual);
		addSearchField("tituloModeloDocumento", SearchCriteria.contendo);
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