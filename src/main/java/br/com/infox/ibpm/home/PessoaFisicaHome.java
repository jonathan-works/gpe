package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.ibpm.entity.PessoaFisica;
import br.com.infox.list.PessoaFisicaList;
import br.com.itx.component.AbstractHome;

@Name(PessoaFisicaHome.NAME)
@Scope(ScopeType.PAGE)
public class PessoaFisicaHome extends AbstractHome<PessoaFisica> {
	private static final long serialVersionUID = 1L;
	private static final String TEMPLATE = "/PessoaFisica/pessoaFisicaTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "PessoaFisica.xls";
	
	public static final String NAME = "pessoaFisicaHome";
	
	@Override
	public EntityList<PessoaFisica> getBeanList() {
		return PessoaFisicaList.instance();
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
