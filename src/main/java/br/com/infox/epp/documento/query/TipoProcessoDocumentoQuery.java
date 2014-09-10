package br.com.infox.epp.documento.query;

public interface TipoProcessoDocumentoQuery {

    String TIPO_PROCESSO_DOCUMENTO_USEABLE = "useableTipoProcessoDocumento";
    String TIPO_PROCESSO_DOCUMENTO_USEABLE_QUERY = "select t from TipoProcessoDocumentoPapel o"
            + " inner join o.tipoProcessoDocumento t"
            + " where t.sistema = false and o.papel=:"
            + TipoProcessoDocumentoQuery.PAPEL_PARAM
            + " and t.ativo = true"
            + " and (t.inTipoDocumento = :"
            + TipoProcessoDocumentoQuery.TIPO_DOCUMENTO_PARAM
            + " or t.inTipoDocumento='T') "
            + "order by t.tipoProcessoDocumento";

    String ASSINATURA_OBRIGATORIA = "assinaturaObrigatoria";
    String ASSINATURA_OBRIGATORIA_QUERY = "select distinct tpdp "
            + "from TipoProcessoDocumentoPapel tpdp "
            + "where tpdp.tipoProcessoDocumento=:"
            + TipoProcessoDocumentoQuery.TIPO_PROCESSO_DOCUMENTO_PARAM
            + " and tpdp.papel=:" + TipoProcessoDocumentoQuery.PAPEL_PARAM;

    String TIPO_PROCESSO_DOCUMENTO_PARAM = "tpProcessoDoc";
    String PAPEL_PARAM = "papel";
    String TIPO_DOCUMENTO_PARAM = "tipoDocumento";

    String LIST_TIPO_PROCESSO_DOCUMENTO = "listTipoProcessoDocumento";
    String LIST_TIPO_PROCESSO_DOCUMENTO_QUERY = "select o from TipoProcessoDocumento o ";
    
    String CODIGO_DOCUMENTO_PARAM = "codigoDocumento";
    String FIND_CLASSIFICACAO_DOCUMENTO_BY_CODIGO = "findClassificacaoDocumentoByCodigo";
    String FIND_CLASSIFICACAO_DOCUMENTO_BY_CODIGO_QUERY = "select o from TipoProcessoDocumento o where o.codigoDocumento=:" + CODIGO_DOCUMENTO_PARAM;
}
