package br.com.infox.epp.cliente.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cliente.entity.Cbo;
import br.com.itx.util.ComponentUtil;

@Name(CboList.NAME)
@Scope(ScopeType.PAGE)
public class CboList extends EntityList<Cbo> {

	public static final String NAME = "cboList";

	private static final long serialVersionUID = 1L;
	
	private static final String TEMPLATE = "/CBO/CboTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "Cbo.xls";

	
	private static final String DEFAULT_EJBQL = "select o from Cbo o";
	private static final String DEFAULT_ORDER = "codCbo";
	
	@Override
	protected void addSearchFields() {
		addSearchField("codCbo", SearchCriteria.IGUAL);
		addSearchField("descricaoCbo", SearchCriteria.CONTENDO);
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
	
	
	public static CboList instance() {
		return ComponentUtil.getComponent(CboList.NAME);
	}
	
	@Override
    public EntityList<Cbo> getBeanList() {
        return CboList.instance();
    }
    
    @Override
    public String getTemplate() {
        return TEMPLATE;
    }
    
    @Override
    public String getDownloadXlsName() {
        return DOWNLOAD_XLS_NAME;
    }
}
