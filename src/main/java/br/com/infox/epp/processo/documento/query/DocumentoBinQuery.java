package br.com.infox.epp.processo.documento.query;

public interface DocumentoBinQuery {
    String QUERY_PARAM_UUID = "uuid";
    String QUERY_PARAM_DOCUMENTO_BIN = "documentoBin";
    
    String GET_BY_UUID = "DocumentoBin.getByUuid";
    String GET_BY_UUID_QUERY = "select o from DocumentoBin o where o.uuid = :" + QUERY_PARAM_UUID;
    
    String GET_DOCUMENTOS_NAO_SUFICIENTEMENTE_ASSINADOS = "DocumentoBin.getDocumentosNaoSuficientementeAssinados";
    String GET_DOCUMENTOS_NAO_SUFICIENTEMENTE_ASSINADOS_QUERY = "select o from Documento o inner join o.documentoBin bin where "
    		+ " bin = :" + QUERY_PARAM_DOCUMENTO_BIN +
    		" and bin.minuta = false and bin.suficientementeAssinado = false";
}
