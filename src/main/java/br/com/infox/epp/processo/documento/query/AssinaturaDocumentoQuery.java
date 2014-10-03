package br.com.infox.epp.processo.documento.query;

public interface AssinaturaDocumentoQuery {

    String PARAM_DOCUMENTO = "documento";
    String TABLE_NAME = "tb_assinatura_documento";
    String SEQUENCE_NAME = "sq_" + TABLE_NAME;
    String COL_ID_ASSINATURA = "id_assinatura";
    String COL_NOME_USUARIO = "nm_usuario";
    String COL_DATA_ASSINATURA = "dt_assinatura";
    String COL_SIGNATURE = "ds_signature";
    String COL_CERT_CHAIN = "ds_cert_chain";
    String COL_NOME_PERFIL = "nm_perfil";

    String LIST_ASSINATURA_DOCUMENTO_BY_DOCUMENTO = "listAssinaturaDocumentoByDocumento";
    String LIST_ASSINATURA_DOCUMENTO_BY_DOCUMENTO_QUERY = "select a "
            + "from AssinaturaDocumento a "
            + "inner join a.documentoBin pdBin " + "where :"
            + PARAM_DOCUMENTO
            + " in elements(pdBin.documentoList)";

}
