package br.com.infox.ibpm.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.ibpm.entity.PessoaFisica;
import br.com.infox.list.PessoaFisicaList;
import br.com.itx.component.Util;
import br.com.itx.component.AbstractHome;
import br.com.itx.exception.ExcelExportException;
import br.com.itx.util.ExcelExportUtil;

@Name(PessoaFisicaHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class PessoaFisicaHome extends AbstractHome<PessoaFisica> {
	
	public static final String NAME = "pessoaFisicaHome";
	private static final long serialVersionUID = 1L;
	
	public static final String TEMPLATE = "/PessoaFisica/pessoaFisicaTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "PessoaFisica.xls";
	private List<PessoaFisica> pessoaFisicaBeanList = new ArrayList<PessoaFisica>();
	
	public void exportarXLS() {
		pessoaFisicaBeanList = PessoaFisicaList.instance().list();
		try {
			if (!pessoaFisicaBeanList.isEmpty()){
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
		map.put("pessoaFisica", pessoaFisicaBeanList);
		ExcelExportUtil.downloadXLS(urlTemplate, map, DOWNLOAD_XLS_NAME);
	}
	
	@Observer("evtCarregarPessoaFisica")
	public void setPessoaFisica(PessoaFisica pessoa){
		setInstance(pessoa);
	}
	
}
