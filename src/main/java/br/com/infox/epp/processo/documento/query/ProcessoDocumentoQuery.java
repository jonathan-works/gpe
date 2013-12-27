package br.com.infox.epp.processo.documento.query;

public interface ProcessoDocumentoQuery {
    
    String NEXT_SEQUENCIAL = "getNextSequencial";
    String NEXT_SEQUENCIAL_QUERY = "select max(pd.numeroDocumento) " +
            "from ProcessoDocumento pd " +
            "inner join pd.tipoProcessoDocumento tpd " +
            "where pd.processo = :processo " +
            "and tpd.numera=true and " +
            "tpd.tipoNumeracao=:tipoNumeracao " +
            "group by pd.processo";

}
