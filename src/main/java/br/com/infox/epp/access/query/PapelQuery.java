package br.com.infox.epp.access.query;

public interface PapelQuery {
    
    String TABLE_PAPEL = "tb_papel";
    String SEQUENCE_PAPEL = "public.sq_tb_papel";
    String ID_PAPEL = "id_papel";
    String NOME_PAPEL = "ds_nome";
    String IDENTIFICADOR = "ds_identificador";
    
    String PARAM_TIPO_MODELO_DOCUMENTO = "tipoModeloDocumento";
    String PAPEIS_NAO_ASSOCIADOS_A_TIPO_MODELO_DOCUMENTO = "listPapeisNaoAssociadosATipoModeloDocumento";
    String PAPEIS_NAO_ASSOCIADOS_A_TIPO_MODELO_DOCUMENTO_QUERY = "select o from Papel o where o.idPapel not in ("
            + "select p.papel.idPapel from TipoModeloDocumentoPapel p "
            + "where p.tipoModeloDocumento = :" + PARAM_TIPO_MODELO_DOCUMENTO + ")";
    
    String PARAM_TIPO_PROCESSO_DOCUMENTO = "tipoProcessoDocumento";
    String PAPEIS_NAO_ASSOCIADOS_A_TIPO_PROCESSO_DOCUMENTO = "listPapeisNaoAssociadosATipoProcessoDocumento";
    String PAPEIS_NAO_ASSOCIADOS_A_TIPO_PROCESSO_DOCUMENTO_QUERY = "select o from Papel o where o not in " +
            "(select p.papel from TipoProcessoDocumentoPapel p " +
            "where p.tipoProcessoDocumento = :" + PARAM_TIPO_PROCESSO_DOCUMENTO + ")";
    
    String PARAM_IDENTIFICADOR = "identificador";
    String PAPEL_BY_IDENTIFICADOR = "findPapelByIdentificador";
    String PAPEL_BY_IDENTIFICADOR_QUERY = "select o from Papel o where o.identificador = :" + PARAM_IDENTIFICADOR;
    
    String PARAM_LISTA_IDENTIFICADORES = "identificadores";
    String PAPEIS_BY_IDENTIFICADORES = "listPapeisByIdentificadores";
    String PAPEIS_BY_IDENTIFICADORES_QUERY = "select p from Papel p where identificador in (:" + PARAM_LISTA_IDENTIFICADORES + ")";

}
