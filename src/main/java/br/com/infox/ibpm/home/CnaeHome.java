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

import br.com.infox.ibpm.entity.Cnae;
import br.com.infox.list.CnaeList;
import br.com.itx.component.AbstractHome;
import br.com.itx.component.Util;
import br.com.itx.exception.ExcelExportException;
import br.com.itx.util.ExcelExportUtil;

@Name(CnaeHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class CnaeHome extends AbstractHome<Cnae>{

	public static final String NAME = "cnaeHome";
	private static final long serialVersionUID = 1L;
	
	public static final String TEMPLATE = "/CNAE/CnaeTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "Cnae.xls";
	private List<Cnae> cnaeBeanList = new ArrayList<Cnae>();
	
	public void exportarXLS() {
		cnaeBeanList = CnaeList.instance().list();
		try {
			if (!cnaeBeanList.isEmpty()){
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
	
	private void exportarXLS (String template) throws ExcelExportException {
		String urlTemplate = new Util().getContextRealPath() + template;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cnae", cnaeBeanList);
		ExcelExportUtil.downloadXLS(urlTemplate, map, DOWNLOAD_XLS_NAME);
	}
	
}
