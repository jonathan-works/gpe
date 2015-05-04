package br.com.infox.epp.processo.documento.query;

public interface PastaRestricaoQuery {

    String PARAM_PASTA = "pasta";
    
    String GET_BY_PASTA = "getRestricaoByPasta";
    String GET_BY_PASTA_QUERY = "select o from PastaRestricao o"
            + " where o.pasta = :" + PARAM_PASTA;
}
