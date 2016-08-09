package br.com.infox.epp.documento.query;

public interface ModeloDocumentoQuery {

    String TABLE_MODELO_DOCUMENTO = "tb_modelo_documento";
    String SEQUENCE_MODELO_DOCUMENTO = "sq_tb_modelo_documento";
    String ID_MODELO_DOCUMENTO = "id_modelo_documento";
    String ID_TIPO_MODELO_DOCUMENTO = "id_tipo_modelo_documento";
    String TITULO_MODELO_DOCUMENTO = "ds_titulo_modelo_documento";
    String CONTEUDO_MODELO_DOCUMENTO = "ds_modelo_documento";

    String LIST_ATIVOS = "listModeloDocumentoAtivo";
    String LIST_ATIVOS_QUERY = "select o from ModeloDocumento o "
            + "where o.ativo = true order by o.tituloModeloDocumento";

    String PARAM_TITULO = "titulo";
    String MODELO_BY_TITULO = "listModeloDocumentoByTitulo";
    String MODELO_BY_TITULO_QUERY = "select o from ModeloDocumento o "
            + "where o.tituloModeloDocumento = :" + PARAM_TITULO;

    String PARAM_GRUPO = "grupo";
    String PARAM_TIPO = "tipo";
    String MODELO_BY_GRUPO_AND_TIPO = "listModeloDocumentoByGrupoAndTipo";
    String MODELO_BY_GRUPO_AND_TIPO_QUERY = "select o from ModeloDocumento o where "
            + "o.tipoModeloDocumento.grupoModeloDocumento = :"
            + PARAM_GRUPO
            + " and o.tipoModeloDocumento = :"
            + PARAM_TIPO
            + " order by o.tituloModeloDocumento";

    String PARAM_LISTA_IDS = "listaDeIdsDeModelos";
    String MODELO_BY_LISTA_IDS = "listModelosByListaIdsModelo";
    String MODELO_BY_LISTA_IDS_QUERY = "select o from ModeloDocumento o "
            + "where o.idModeloDocumento in (:" + PARAM_LISTA_IDS
            + ") order by modeloDocumento";

}
