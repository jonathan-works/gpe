package br.com.infox.epp.pessoa.manager;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PessoaService {

    @Inject
    private PessoaFisicaManager pessoaFisicaManager;
    @Inject
    private PessoaJuridicaManager pessoaJuridicaManager;

    public void persist(Pessoa pessoa) {
        if (pessoa == null) {
            throw new IllegalArgumentException("Pessoa cannot be null");
        }
        if ( TipoPessoaEnum.F.equals(pessoa.getTipoPessoa()) ) {
            pessoaFisicaManager.persist((PessoaFisica) pessoa);
        } else if ( TipoPessoaEnum.J.equals(pessoa.getTipoPessoa()) ) {
            pessoaJuridicaManager.persist((PessoaJuridica) pessoa);
        } else {
            throw new IllegalStateException("Tipo de pessoa desconhecido " + pessoa.getTipoPessoa());
        }
    }

    public Pessoa getByCodigo(String codigo, TipoPessoaEnum tipoPessoa) {
        if (codigo == null || tipoPessoa == null) {
            throw new IllegalArgumentException("Codigo cannot be null");
        }
        if ( TipoPessoaEnum.F.equals(tipoPessoa) ) {
            return pessoaFisicaManager.getByCpf(codigo);
        } else if ( TipoPessoaEnum.J.equals(tipoPessoa) ) {
            return pessoaJuridicaManager.getByCnpj(codigo);
        }
        throw new IllegalStateException("Impossível pesquisar por código para o tipo de pessoa " + tipoPessoa);
    }

}
