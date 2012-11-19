package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.ibpm.entity.PessoaJuridica;
import br.com.itx.component.AbstractHome;

@Name(PessoaJuridicaHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class PessoaJuridicaHome extends AbstractHome<PessoaJuridica>{

	public static final String NAME = "pessoaJuridicaHome";
	private static final long serialVersionUID = 1L;
	
	@Observer("evtCarregarPessoaJuridica")
	public void setPessoaJuridica(PessoaJuridica pessoa){
		setInstance(pessoa);
	}
	
}
