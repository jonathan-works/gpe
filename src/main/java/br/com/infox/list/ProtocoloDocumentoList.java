package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.ProtocoloDocumento;

@Name(ProtocoloDocumentoList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class ProtocoloDocumentoList extends EntityList<ProtocoloDocumento> {

	
	public static final String NAME = "protocoloDocumentoList";

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from ProtocoloDocumento o";
	private static final String DEFAULT_ORDER = "idProtocoloDocumento";
	
	@Override
	protected void addSearchFields() {
		addSearchField("documentoFisico", SearchCriteria.IGUAL);
		addSearchField("nomePessoa", SearchCriteria.CONTENDO);
		addSearchField("dataEntrada", SearchCriteria.DATA_IGUAL);
		addSearchField("dataSaida", SearchCriteria.DATA_IGUAL);
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
