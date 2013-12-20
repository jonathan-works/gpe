package br.com.infox.epp.documento.query;

public interface ModeloDocumentoQuery {

    String TABLE_MODELO_DOCUMENTO = "tb_modelo_documento";
    String SEQUENCE_MODELO_DOCUMENTO = "public.sq_tb_modelo_documento";
    String ID_MODELO_DOCUMENTO = "id_modelo_documento";
    String ID_TIPO_MODELO_DOCUMENTO = "id_tipo_modelo_documento";
    String TITULO_MODELO_DOCUMENTO = "ds_titulo_modelo_documento";
    String CONTEUDO_MODELO_DOCUMENTO = "ds_modelo_documento";

    String LIST_ATIVOS = "listModeloDocumentoAtivo";
    String LIST_ATIVOS_QUERY = "select o from ModeloDocumento o "
            + "where o.ativo = true";
    
    String PARAM_TITULO = "titulo";
    String MODELO_BY_TITULO = "listModeloDocumentoByTitulo";
    String MODELO_BY_TITULO_QUERY = "select o from ModeloDocumento o "
                                    + "where o.tituloModeloDocumento = :" + PARAM_TITULO;
    
    
    String PARAM_GRUPO = "grupo";
    String PARAM_TIPO = "tipo";
    String MODELO_DOCUMENTO_BY_GRUPO_AND_TIPO = "listModeloDocumentoByGrupoAndTipo";
    String MODELO_DOCUMENTO_BY_GRUPO_AND_TIPO_QUERY = "select distinct o from ModeloDocumento o where "
            + "o.tipoModeloDocumento.grupoModeloDocumento = :" + PARAM_GRUPO
            + " and o.tipoModeloDocumento = :" + PARAM_TIPO
            + " order by o.tituloModeloDocumento";

}
