package br.com.infox.epp.documento.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;
import br.com.itx.util.ComponentUtil;

@Name(GrupoModeloDocumentoList.NAME)
@Scope(ScopeType.CONVERSATION)
public class GrupoModeloDocumentoList extends EntityList<GrupoModeloDocumento> {

	public static final String NAME = "grupoModeloDocumentoList";
	private static final long serialVersionUID = 1L;
	
	private static final String TEMPLATE = "/GrupoModeloDocumento/grupoModeloDocumentoTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "GruposModeloDocumento.xls";
	
	private static final String DEFAULT_EJBQL = "select o from GrupoModeloDocumento o";
	private static final String DEFAULT_ORDER = "grupoModeloDocumento";
	
	public static final GrupoModeloDocumentoList instance() {
		return ComponentUtil.getComponent(NAME);
	}
	
	protected void addSearchFields() {
		addSearchField("grupoModeloDocumento", SearchCriteria.CONTENDO);
		addSearchField("ativo", SearchCriteria.IGUAL);
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
	
	@Override
    public String getDownloadXlsName() {
        return DOWNLOAD_XLS_NAME;
    }

    @Override
    public String getTemplate() {
        return TEMPLATE;
    }

    @Override
    public EntityList<GrupoModeloDocumento> getBeanList() {
        return GrupoModeloDocumentoList.instance();
    }
}