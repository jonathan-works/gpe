package br.com.infox.ibpm.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.ibpm.entity.Cbo;
import br.com.infox.list.CboList;
import br.com.itx.component.AbstractHome;
import br.com.itx.component.Util;
import br.com.itx.exception.ExcelExportException;
import br.com.itx.util.ExcelExportUtil;

@Name(CboHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class CboHome extends AbstractHome<Cbo>{

	public static final String NAME = "cboHome";
	private static final long serialVersionUID = 1L;
	
	public static final String TEMPLATE = "/CBO/CboTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "Cbo.xls";
	private List<Cbo> cboBeanList = new ArrayList<Cbo>();
	
	public void exportarXLS() {
		cboBeanList = CboList.instance().list();
		try {
			if (!cboBeanList.isEmpty()){
				exportarXLS(TEMPLATE);
			}
			else{
				FacesMessages.instance().add(Severity.INFO, "Não há dados para exportar!");
			}
		} catch (ExcelExportException e) {
			FacesMessages.instance().add(Severity.ERROR, "Erro ao exportar arquivo." + e.getMessage());
			e.printStackTrace();
		}	
	}
	
	public void exportarXLS (String template) throws ExcelExportException {
		String urlTemplate = new Util().getContextRealPath() + template;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cbo", cboBeanList);
		ExcelExportUtil.downloadXLS(urlTemplate, map, DOWNLOAD_XLS_NAME);
	}
	
}
