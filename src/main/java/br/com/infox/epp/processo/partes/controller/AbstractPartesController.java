package br.com.infox.epp.processo.partes.controller;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.pessoa.manager.PessoaJuridicaManager;

@Scope(ScopeType.CONVERSATION)
abstract class AbstractPartesController {

    private PessoaFisica pessoaFisica = new PessoaFisica();
    private PessoaJuridica pessoaJuridica = new PessoaJuridica();

    @In
    private PessoaFisicaManager pessoaFisicaManager;
    @In
    private PessoaJuridicaManager pessoaJuridicaManager;

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public void searchByCpf() {
        final String cpf = getPessoaFisica().getCpf();
        setPessoaFisica(pessoaFisicaManager.getByCpf(cpf));
        if (getPessoaFisica() == null) {
            setPessoaFisica(new PessoaFisica());
            getPessoaFisica().setCpf(cpf);
            getPessoaFisica().setAtivo(true);
        }
    }

    public void searchByCnpj() {
        final String cnpj = getPessoaJuridica().getCnpj();
        setPessoaJuridica(pessoaJuridicaManager.getByCnpj(cnpj));
        if (getPessoaJuridica() == null) {
            setPessoaJuridica(new PessoaJuridica());
            getPessoaJuridica().setCnpj(cnpj);
            getPessoaJuridica().setAtivo(true);
        }
    }

    public abstract boolean podeAdicionarPartes();

    public abstract void includePessoaFisica();

    public abstract void includePessoaJuridica();

    public abstract boolean apenasPessoaFisica();

    public abstract boolean apenasPessoaJuridica();

}
