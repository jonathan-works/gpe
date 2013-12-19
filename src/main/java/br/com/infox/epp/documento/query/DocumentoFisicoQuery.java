package br.com.infox.epp.documento.query;

public interface DocumentoFisicoQuery {

    String TABLE_DOCUMENTO_FISICO = "tb_documento_fisico";
    String SEQUENCE_DOCUMENTO_FISICO = "sq_tb_documento_fisico";
    String ID_DOCUMENTO_FISICO = "id_documento_fisico";
    String ID_LOCALIZACAO_FISICA = "id_localizacao_fisica";
    String ID_PROCESSO = "id_processo";
    String DOCUMENTO_FISICO = "ds_documento_fisico";

    String QUERY_PARAM_PROCESSO = "processo";

    String LIST_BY_PROCESSO = "listDocumentoFisicoByProcesso";
    String LIST_BY_PROCESSO_QUERY = "select o from DocumentoFisico o "
            + "where o.ativo = true and " + "o.processo = :"
            + QUERY_PARAM_PROCESSO;

}
