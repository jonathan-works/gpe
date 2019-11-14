package br.com.infox.epp.processo.documento.query;

public interface PastaRestricaoQuery {

    String PARAM_PASTA = "pasta";
    String PARAM_ALVO = "alvo";
    String PARAM_TIPO_RESTRICAO = "tipoRestricao";
    
    String GET_BY_PASTA = "getRestricaoByPasta";
    String GET_BY_PASTA_QUERY = "select o from PastaRestricao o "
            + "where o.pasta = :" + PARAM_PASTA;
    
    String DELETE_BY_PASTA = "deleteByPasta";
    String DELETE_BY_PASTA_QUERY = "delete from PastaRestricao o "
            + "where o.pasta = :" + PARAM_PASTA;
    
    String GET_BY_PASTA_ALVO_TIPO_RESTRICAO = "getByPastaAlvoTipoRestricao";
    String GET_BY_PASTA_ALVO_TIPO_RESTRICAO_QUERY = "select o from PastaRestricao o where o.pasta = :" + PARAM_PASTA 
    		+ " and o.alvo = :" + PARAM_ALVO + " and o.tipoPastaRestricao = :" + PARAM_TIPO_RESTRICAO;
}
