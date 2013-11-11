package br.com.infox.epp.processo.documento.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;

@Name(ProcessoDocumentoList.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoDocumentoList extends EntityList<ProcessoDocumento> {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_EJBQL = "select o from ProcessoDocumento o";
    private static final String DEFAULT_ORDER = "dataInclusao desc";
    private static final String R1 = "o.processo.idProcesso = #{processoHome.id}";
	
	public static final String NAME = "processoDocumentoList";

	@Override
	protected void addSearchFields() {
		addSearchField("idProcesso", SearchCriteria.IGUAL, ProcessoDocumentoList.R1);
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
