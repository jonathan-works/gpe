package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.TipoProcessoDocumento;
import br.com.itx.util.ComponentUtil;

@Name(TipoProcessoDocumentoList.NAME)
@Scope(ScopeType.PAGE)
public class TipoProcessoDocumentoList extends
		EntityList<TipoProcessoDocumento> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "tipoProcessoDocumentoList";
	
	private static final String DEFAULT_EJBQL = "select o from TipoProcessoDocumento o";
	private static final String DEFAULT_ORDER = "tipoProcessoDocumento";
	
	@Override
	protected void addSearchFields() {
		addSearchField("codigoDocumento", SearchCriteria.contendo);
		addSearchField("tipoProcessoDocumento", SearchCriteria.contendo);
		addSearchField("inTipoDocumento", SearchCriteria.igual);
		addSearchField("visibilidade", SearchCriteria.igual);
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
		// TODO Auto-generated method stub
		return null;
	}
	
	public static TipoProcessoDocumentoList instance(){
		return ComponentUtil.getComponent(TipoProcessoDocumentoList.NAME);
	}

}
