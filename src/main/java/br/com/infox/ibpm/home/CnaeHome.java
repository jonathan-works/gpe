package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.ibpm.entity.Cnae;
import br.com.infox.list.CnaeList;
import br.com.itx.component.AbstractHome;

@Name(CnaeHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class CnaeHome extends AbstractHome<Cnae>{
	public static final String NAME = "cnaeHome";
	private static final long serialVersionUID = 1L;
	
	private static final String TEMPLATE = "/CNAE/CnaeTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "Cnae.xls";

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
