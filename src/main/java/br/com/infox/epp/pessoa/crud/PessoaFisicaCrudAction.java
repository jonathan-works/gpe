package br.com.infox.epp.pessoa.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.pessoa.entity.PessoaFisica;

@Name(PessoaFisicaCrudAction.NAME)
public class PessoaFisicaCrudAction extends AbstractCrudAction<PessoaFisica> {
	
	public static final String NAME = "pessoaFisicaCrudAction";

}
