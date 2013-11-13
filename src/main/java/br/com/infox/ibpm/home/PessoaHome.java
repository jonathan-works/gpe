package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.type.TipoPessoaEnum;
import br.com.itx.component.AbstractHome;

@Name(PessoaHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class PessoaHome extends AbstractHome<Pessoa> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "pessoaHome";
	
	private TipoPessoaEnum tipoPessoa;
	
	public TipoPessoaEnum[] getTipoPessoaItens(){
		return TipoPessoaEnum.values();
	}

	public TipoPessoaEnum getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(TipoPessoaEnum tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}
	
	public boolean isPessoaFisica(){
		return this.tipoPessoa == TipoPessoaEnum.F;
	}
	
	public boolean isPessoaJuridica(){
		return this.tipoPessoa == TipoPessoaEnum.J;
	}
}
