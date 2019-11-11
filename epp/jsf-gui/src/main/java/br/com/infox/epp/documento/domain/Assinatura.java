package br.com.infox.epp.documento.domain;

import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.pessoa.entity.PessoaFisica;

public interface Assinatura {

    PessoaFisica getPessoaFisica();

    Papel getPapel();

}
