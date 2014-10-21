package br.com.infox.epp.processo.documento.query;

public interface DocumentoBinQuery {
    String QUERY_PARAM_UUID = "uuid";
    
    String GET_BY_UUID = "DocumentoBin.getByUuid";
    String GET_BY_UUID_QUERY = "select o from DocumentoBin o where o.uuid = :" + QUERY_PARAM_UUID;
}
