package br.com.infox.epp.documento.query;

public interface TipoModeloDocumentoPapelQuery {

    String TABLE_TIPO_MODELO_DOCUMENTO_PAPEL = "tb_tipo_modelo_documento_papel";
    String SEQUENCE_TIPO_MODELO_DOCUMENTO_PAPEL = "sq_tipo_modelo_documento_papel";
    String ID_TIPO_MODELO_DOCUMENTO_PAPEL = "id_tipo_modelo_documento_papel";
    String ID_TIPO_MODELO_DOCUMENTO = "id_tipo_modelo_documento";
    String ID_PAPEL = "id_papel";

    String PAPEL_PARAM = "papel";
    String TIPOS_MODELO_DOCUMENTO_PERMITIDOS = "tiposDeModeloDcumentoPermitidos";
    String TIPOS_MODELO_DOCUMENTO_PERMITIDOS_QUERY = "select t from TipoModeloDocumentoPapel tmdp "
            + "join tmdp.tipoModeloDocumento t where tmdp.papel = :"
            + PAPEL_PARAM + " order by t.tipoModeloDocumento";

}
