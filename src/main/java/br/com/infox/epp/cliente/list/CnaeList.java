package br.com.infox.epp.cliente.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cliente.entity.Cnae;
import br.com.itx.util.ComponentUtil;

@Name(CnaeList.NAME)
@Scope(ScopeType.PAGE)
public class CnaeList extends EntityList<Cnae> {

	public static final String NAME = "cnaeList";

	private static final long serialVersionUID = 1L;
	
	private static final String TEMPLATE = "/CNAE/CnaeTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "Cnae.xls";
	
	private static final String DEFAULT_EJBQL = "select o from Cnae o";
	private static final String DEFAULT_ORDER = "codCnae";
	
	@Override
	protected void addSearchFields() {
		addSearchField("codCnae", SearchCriteria.IGUAL);
		addSearchField("descricaoCnae", SearchCriteria.CONTENDO);
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
	
	public static CnaeList instance(){
		return ComponentUtil.getComponent(CnaeList.NAME);
	}
	
    @Override
    public EntityList<Cnae> getBeanList() {
        return CnaeList.instance();
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
