package br.com.infox.epp.pessoa.crud;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.manager.PessoaJuridicaManager;

@Named
@ViewScoped
public class PessoaJuridicaCrudAction extends AbstractCrudAction<PessoaJuridica, PessoaJuridicaManager> {
    private static final long serialVersionUID = 1L;

    @Inject
    private PessoaJuridicaManager pessoaJuridicaManager;

    @Override
    protected PessoaJuridicaManager getManager() {
        return pessoaJuridicaManager;
    }
}
