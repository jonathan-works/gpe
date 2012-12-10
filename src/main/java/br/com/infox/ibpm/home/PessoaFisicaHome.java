package br.com.infox.ibpm.home;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import br.com.infox.ibpm.entity.PessoaFisica;
import br.com.infox.list.PessoaFisicaList;
import br.com.itx.component.AbstractHome;

@Name(PessoaFisicaHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class PessoaFisicaHome extends AbstractHome<PessoaFisica> {
	private static final long serialVersionUID = 1L;
	private static final String TEMPLATE = "/PessoaFisica/pessoaFisicaTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "PessoaFisica.xls";
	
	public static final String NAME = "pessoaFisicaHome";
	
	@Override
	public List<PessoaFisica> getBeanList() {
		return PessoaFisicaList.instance().list();
	}
	
	@Override
	public String getTemplate() {
		return TEMPLATE;
	}
	
	@Override
	public String getDownloadXlsName() {
		return DOWNLOAD_XLS_NAME;
	}
	
	@Observer("evtCarregarPessoaFisica")
	public void setPessoaFisica(PessoaFisica pessoa){
		setInstance(pessoa);
	}
	
}
