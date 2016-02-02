package br.com.infox.epp.documento.query;

public interface HistoricoModeloDocumentoQuery {

    String TABLE_HISTORICO_MODELO_DOCUMENTO = "tb_modelo_documento_historico";
    String SEQUENCE_HISTORICO_MODELO_DOCUMENTO = "sq_modelo_documento_historico";
    String ID_HISTORICO_MODELO_DOCUMENTO = "id_modelo_documento_historico";
    String ID_MODELO_DOCUMENTO = "id_modelo_documento";
    String ID_USUARIO_ALTERACAO = "id_usuario_alteracao";
    String DATA_ALTERACAO = "dt_alteracao";
    String TITULO_MODELO_DOCUMENTO = "ds_titulo_modelo_documento";
    String CONTEUDO_MODELO_DOCUMENTO = "ds_modelo_documento";

    String LIST_MODELO = "listModeloDocumento";
    String LIST_MODELO_QUERY = "select distinct o.modeloDocumento from HistoricoModeloDocumento o";

    String LIST_USUARIO = "listUsarioAlteracao";
    String LIST_USUARIO_PARAM_MODELO = "modeloDocumento";
    String LIST_USUARIO_QUERY = "select distinct o.usuarioAlteracao from HistoricoModeloDocumento o where o.modeloDocumento=:"
            + LIST_USUARIO_PARAM_MODELO;

}
