package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.itx.component.AbstractHome;

@Name(PessoaFisicaHome.NAME)
@Scope(ScopeType.PAGE)
public class PessoaFisicaHome extends AbstractHome<PessoaFisica> {
	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "pessoaFisicaHome";
	
	@Observer(PessoaFisica.EVENT_LOAD)
	public void setPessoaFisica(PessoaFisica pessoa){
		setInstance(pessoa);
	}
	
}
