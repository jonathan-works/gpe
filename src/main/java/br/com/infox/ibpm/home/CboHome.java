package br.com.infox.ibpm.home;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import br.com.infox.ibpm.entity.Cbo;
import br.com.infox.list.CboList;
import br.com.itx.component.AbstractHome;

@Name(CboHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class CboHome extends AbstractHome<Cbo>{

	public static final String NAME = "cboHome";
	private static final long serialVersionUID = 1L;
	
	private static final String TEMPLATE = "/CBO/CboTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "Cbo.xls";

	@Override
	public List<Cbo> getBeanList() {
		return CboList.instance().list();
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
