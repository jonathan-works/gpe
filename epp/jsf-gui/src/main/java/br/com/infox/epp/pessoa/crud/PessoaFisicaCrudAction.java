package br.com.infox.epp.pessoa.crud;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;

@Named
@ViewScoped
public class PessoaFisicaCrudAction extends AbstractCrudAction<PessoaFisica, PessoaFisicaManager> {

    private static final long serialVersionUID = 1L;

    @Inject
    private PessoaFisicaManager pessoaFisicaManager;

    @Override
    protected PessoaFisicaManager getManager() {
        return pessoaFisicaManager;
    }
}
