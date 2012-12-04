package br.com.infox.ibpm.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.epa.entity.Categoria;
import br.com.infox.epa.list.CategoriaList;
import br.com.itx.component.AbstractHome;
import br.com.itx.component.Util;
import br.com.itx.exception.ExcelExportException;
import br.com.itx.util.ExcelExportUtil;

/**
 * 
 * @author Daniel
 *
 */
@Name(CategoriaHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class CategoriaHome extends AbstractHome<Categoria> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "categoriaHome";
	
	public static final String TEMPLATE = "/Categoria/CategoriaTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "Categoria.xls";
	private List<Categoria> categoriaBeanList = new ArrayList<Categoria>();
	
	public void exportarXLS() {
		categoriaBeanList = CategoriaList.instance().list();
		try {
			if (!categoriaBeanList.isEmpty()){
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
		map.put("categoria", categoriaBeanList);
		ExcelExportUtil.downloadXLS(urlTemplate, map, DOWNLOAD_XLS_NAME);
	}
	
}