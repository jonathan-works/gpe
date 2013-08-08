package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.Cnae;
import br.com.itx.util.ComponentUtil;

@Name(CnaeList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class CnaeList extends EntityList<Cnae> {

	public static final String NAME = "cnaeList";

	private static final long serialVersionUID = 1L;
	
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
}
