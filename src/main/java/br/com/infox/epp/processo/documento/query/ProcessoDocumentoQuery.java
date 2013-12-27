package br.com.infox.epp.processo.documento.query;

public interface ProcessoDocumentoQuery {
    
    String PARAM_PROCESSO = "processo";
    String PARAM_TIPO_PROCESSO = "tipoNumeracao";
    
    String NEXT_SEQUENCIAL = "getNextSequencial";
    String NEXT_SEQUENCIAL_QUERY = "select max(pd.numeroDocumento) " +
            "from ProcessoDocumento pd " +
            "inner join pd.tipoProcessoDocumento tpd " +
            "where pd.processo = :" + PARAM_PROCESSO +
            " and tpd.numera=true and " +
            "tpd.tipoNumeracao=:" + PARAM_TIPO_PROCESSO +
            " group by pd.processo";

}
