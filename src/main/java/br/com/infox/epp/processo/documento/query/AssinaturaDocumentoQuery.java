package br.com.infox.epp.processo.documento.query;

public interface AssinaturaDocumentoQuery {

    String TABLE_NAME = "tb_assinatura_documento";
    String SEQUENCE_NAME = "sq_" + TABLE_NAME;
    String COL_ID_ASSINATURA = "id_assinatura";
    String COL_NOME_USUARIO = "nm_usuario";
    String COL_DATA_ASSINATURA = "dt_assinatura";
    String COL_SIGNATURE = "ds_signature";
    String COL_CERT_CHAIN = "ds_cert_chain";
    String COL_NOME_PAPEL = "nm_papel";
    String COL_NOME_LOCALIZACAO = "nm_localizacao";

}
