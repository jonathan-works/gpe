package br.com.infox.epp.julgamento.query;

public interface StatusSessaoJulgamentoQuery {
    
    String PARAM_NOME_STATUS = "nomeStatus";
    
    String GET_STATUS_SESSAO_JULGAMENTO_BY_NOME = "getStatusSessaoJulgamentoByNome";
    String GET_STATUS_SESSAO_JULGAMENTO_BY_NOME_QUERY = "select o from StatusSessaoJulgamento o " +
            " where lower(o.nome) = lower( :" + PARAM_NOME_STATUS + " )";

}
