package br.com.infox.epp.pessoa.query;

public interface PessoaFisicaQuery {

    String CPF_PARAM = "cpf";
    String LOCALIZACAO_PARAM = "localizacao";
    String SEARCH_BY_CPF = "searchByCpf";
    String SEARCH_BY_CPF_QUERY = "select o from PessoaFisica o where o.cpf = :"
            + CPF_PARAM;
    
}
