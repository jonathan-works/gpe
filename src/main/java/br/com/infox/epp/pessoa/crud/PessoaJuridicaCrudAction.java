package br.com.infox.epp.pessoa.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;

@Name(PessoaJuridicaCrudAction.NAME)
public class PessoaJuridicaCrudAction extends
		AbstractCrudAction<PessoaJuridica> {
	
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static final String NAME = "pessoaJuridicaCrudAction";

}
