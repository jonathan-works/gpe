package br.com.infox.epp.documento.query;

public interface TipoModeloDocumentoQuery {

    String TABLE_TIPO_MODELO_DOCUMENTO = "tb_tipo_modelo_documento";
    String SEQUENCE_TIPO_MODELO_DOCUMENTO = "sq_tb_tipo_modelo_documento";
    String ID_TIPO_MODELO_DOCUMENTO = "id_tipo_modelo_documento";
    String ID_GRUPO_MODELO_DOCUMENTO = "id_grupo_modelo_documento";
    String TIPO_MODELO_DOCUMENTO = "ds_tipo_modelo_documento";
    String ABREVIACAO = "ds_abreviacao";
    String TIPO_MODELO_DOCUMENTO_ATTRIBUTE = "tipoModeloDocumento";

    String LIST_TIPOS_MODELO_DOCUMENTO_ATIVOS = "TipoModeloDocumento.listTiposModeloDocumentoAtivos";
    String LIST_TIPOS_MODELO_DOCUMENTO_ATIVOS_QUERY = "select o from TipoModeloDocumento o where o.ativo = true order by o.tipoModeloDocumento";
}
