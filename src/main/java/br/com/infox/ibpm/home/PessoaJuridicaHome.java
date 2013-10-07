package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.ibpm.entity.PessoaJuridica;
import br.com.infox.list.PessoaJuridicaList;
import br.com.itx.component.AbstractHome;

@Name(PessoaJuridicaHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class PessoaJuridicaHome extends AbstractHome<PessoaJuridica>{
	private static final long serialVersionUID = 1L;
	private static final String TEMPLATE = "/PessoaJuridica/pessoaJuridicaTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "PessoaJuridica.xls";

	public static final String NAME = "pessoaJuridicaHome";
	
	@Override
	public EntityList<PessoaJuridica> getBeanList() {
		return PessoaJuridicaList.instance();
	}
	
	@Override
	public String getTemplate() {
		return TEMPLATE;
	}
	
	@Override
	public String getDownloadXlsName() {
		return DOWNLOAD_XLS_NAME;
	}
	
	@Observer("evtCarregarPessoaJuridica")
	public void setPessoaJuridica(PessoaJuridica pessoa){
		setInstance(pessoa);
	}
	
}
