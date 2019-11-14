package br.com.infox.epp.pessoa.query;

public interface PessoaJuridicaQuery {

    String CNPJ_PARAM = "cnpj";
    String SEARCH_BY_CNPJ = "searchByCnpj";
    String SEARCH_BY_CNPJ_QUERY = "select o from PessoaJuridica o where o.cnpj = :"
            + CNPJ_PARAM;

}
