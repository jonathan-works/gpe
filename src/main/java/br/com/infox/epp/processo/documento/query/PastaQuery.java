package br.com.infox.epp.processo.documento.query;

public interface PastaQuery {
    String PARAM_PROCESSO = "processo";
    
    String GET_BY_PROCESSO = "getByProcesso";
    String GET_BY_PROCESSO_QUERY = "select o from Pasta o where o.processo = :" + PARAM_PROCESSO
            + " order by o.nome";
    String GET_DEFAULT_BY_PROCESSO = "getDefaultByProcesso";
    String GET_DEFAULT_BY_PROCESSO_QUERY = "select o from Pasta o where o.processo = :" + PARAM_PROCESSO
            + " order by o.id"; 
            
}
