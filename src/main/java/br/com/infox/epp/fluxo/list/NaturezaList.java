package br.com.infox.epp.fluxo.list;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.itx.util.ComponentUtil;

@Name(NaturezaList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class NaturezaList extends EntityList<Natureza> {
	
	public static final String NAME = "naturezaList";

	private static final long serialVersionUID = 1L;
	
	private static final String TEMPLATE = "/Natureza/NaturezaTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "Naturezas.xls";
	
	private static final String DEFAULT_EJBQL = "select o from Natureza o";
	private static final String DEFAULT_ORDER = "natureza";

	public static final NaturezaList instance() {
		return ComponentUtil.getComponent(NaturezaList.NAME);
	}
	
	@Override
	protected void addSearchFields() {
		addSearchField("natureza", SearchCriteria.CONTENDO);
		addSearchField("ativo", SearchCriteria.IGUAL);
		addSearchField("hasPartes", SearchCriteria.IGUAL);
	}

	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("fluxo", "fluxo.fluxo");
		return map;
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
    public String getTemplate() {
        return TEMPLATE;
    }
    
    @Override
    public String getDownloadXlsName() {
        return DOWNLOAD_XLS_NAME;
    }
    
    @Override
    public EntityList<Natureza> getBeanList() {
        return NaturezaList.instance();
    }
	
}