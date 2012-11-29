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

import br.com.infox.core.action.list.EntityList;
import br.com.infox.ibpm.entity.PessoaJuridica;
import br.com.infox.list.PessoaJuridicaList;
import br.com.itx.component.AbstractHome;
import br.com.itx.component.Util;
import br.com.itx.exception.ExcelExportException;
import br.com.itx.util.ExcelExportUtil;

@Name(PessoaJuridicaHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class PessoaJuridicaHome extends AbstractHome<PessoaJuridica>{

	public static final String NAME = "pessoaJuridicaHome";
	private static final long serialVersionUID = 1L;
	
	public static final String TEMPLATE = "/PessoaJuridica/pessoaJuridicaTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "PessoaJuridica.xls";
	private List<PessoaJuridica> pessoaJuridicaBeanList = new ArrayList<PessoaJuridica>();
	
	public void exportarXLS() {
		pessoaJuridicaBeanList = PessoaJuridicaList.instance().list();
		try {
			if (!pessoaJuridicaBeanList.isEmpty()){
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
		map.put("pessoaJuridica", pessoaJuridicaBeanList);
		ExcelExportUtil.downloadXLS(urlTemplate, map, DOWNLOAD_XLS_NAME);
	}
	
	@Observer("evtCarregarPessoaJuridica")
	public void setPessoaJuridica(PessoaJuridica pessoa){
		setInstance(pessoa);
	}
	
}
