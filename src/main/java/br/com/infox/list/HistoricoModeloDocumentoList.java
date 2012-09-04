package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.HistoricoModeloDocumento;

@Name(HistoricoModeloDocumentoList.NAME)
@Scope(ScopeType.PAGE)
public class HistoricoModeloDocumentoList extends EntityList<HistoricoModeloDocumento>{
	
	public static final String NAME = "historicoModeloDocumentoList";

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from HistoricoModeloDocumento o";
	private static final String DEFAULT_ORDER = "nome";

	@Override
	protected void addSearchFields() {
		addSearchField("modeloDocumento", SearchCriteria.igual);
		addSearchField("usuarioAlteracao", SearchCriteria.igual);
		addSearchField("dataAlteracao", SearchCriteria.dataIgual);
		addSearchField("tituloModeloDocumento", SearchCriteria.contendo);
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
