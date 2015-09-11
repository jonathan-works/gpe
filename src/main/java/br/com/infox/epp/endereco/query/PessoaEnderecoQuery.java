package br.com.infox.epp.endereco.query;

public interface PessoaEnderecoQuery {

    String PARAM_PESSOA = "pessoa";
    
    String PESSOA_ENDERECO_BY_PESSOA = "pessoaEnderecoByPessoa";
    String PESSOA_ENDERECO_BY_PESSOA_QUERY = "select o from PessoaEndereco o "
            + "where o.pessoa = :" + PARAM_PESSOA;
    
}
