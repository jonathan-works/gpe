package br.com.infox.epp.documento.query;

public interface ClassificacaoDocumentoQuery {
	
	String CLASSIFICACAO_DOCUMENTO_PARAM = "classificacaoDocumento";
    String PAPEL_PARAM = "papel";
    String TIPO_DOCUMENTO_PARAM = "tipoDocumento";

    String CLASSIFICACAO_DOCUMENTO_USEABLE = "useableTipoProcessoDocumento";
    String CLASSIFICACAO_DOCUMENTO_USEABLE_QUERY = "select t from ClassificacaoDocumentoPapel o "
            + " inner join o.classificacaoDocumento t "
            + " where t.sistema = false and o.papel=:" + PAPEL_PARAM
            + " and t.ativo = true "
            + " and (t.inTipoDocumento = :" + TIPO_DOCUMENTO_PARAM
            + " or t.inTipoDocumento = 'T') "
            + "order by t.descricao";

    String ASSINATURA_OBRIGATORIA = "assinaturaObrigatoria";
    String ASSINATURA_OBRIGATORIA_QUERY = "select distinct tpdp "
            + "from ClassificacaoDocumentoPapel tpdp "
            + "where tpdp.classificacaoDocumento = :" + CLASSIFICACAO_DOCUMENTO_PARAM
            + " and tpdp.papel = :" + PAPEL_PARAM;


    String LIST_CLASSIFICACAO_DOCUMENTO = "listClassificacaoDocumento";
    String LIST_CLASSIFICACAO_DOCUMENTO_QUERY = "select o from ClassificacaoDocumento o ";
    
    String CODIGO_DOCUMENTO_PARAM = "codigoDocumento";
    String FIND_CLASSIFICACAO_DOCUMENTO_BY_CODIGO = "findClassificacaoDocumentoByCodigo";
    String FIND_CLASSIFICACAO_DOCUMENTO_BY_CODIGO_QUERY = "select o from ClassificacaoDocumento o where o.codigoDocumento = :" + CODIGO_DOCUMENTO_PARAM;
}
