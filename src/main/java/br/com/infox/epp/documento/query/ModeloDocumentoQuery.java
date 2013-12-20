package br.com.infox.epp.documento.query;

/**
 * Interface com as queries da entidade ModeloDocumento
 * 
 * @author erikliberal
 */
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

}
