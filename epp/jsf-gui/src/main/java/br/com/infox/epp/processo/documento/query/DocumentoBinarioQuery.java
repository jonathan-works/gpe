package br.com.infox.epp.processo.documento.query;

public interface DocumentoBinarioQuery {
    String QUERY_PARAM_ID_DOCUMENTO_BIN = "idDocumentoBin";
    
    String EXISTE_BINARIO = "DocumentoBinario.existeBinario";
    String EXISTE_BINARIO_QUERY = "select 1 from DocumentoBinario where id = :" + QUERY_PARAM_ID_DOCUMENTO_BIN;
}
