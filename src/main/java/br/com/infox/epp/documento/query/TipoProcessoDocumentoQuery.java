package br.com.infox.epp.documento.query;

public interface TipoProcessoDocumentoQuery {
    
    String DOCUMENTO_INTERNO_BASE_QUERY = "select o from TipoProcessoDocumento o " +
                "where o.ativo = true and (o.visibilidade = 'I' OR o.visibilidade = 'A') and " +
                "(o.inTipoDocumento = ";
    String DOCUMENTO_INTERNO_SUFIX = " OR o.inTipoDocumento = 'T')";
    
    String ASSINATURA_OBRIGATORIA ="assinaturaObrigatoria";
    String ASSINATURA_OBRIGATORIA_QUERY = "select distinct tpdp.obrigatorio " +
            "from TipoProcessoDocumentoPapel tpdp " +
            "where tpdp.tipoProcessoDocumento=:" + 
            TipoProcessoDocumentoQuery.TIPO_PROCESSO_DOCUMENTO_PARAM +
            " and tpdp.papel=:" +
            TipoProcessoDocumentoQuery.PAPEL_PARAM;
    
    String TIPO_PROCESSO_DOCUMENTO_PARAM = "tpProcessoDoc";
    String PAPEL_PARAM = "papel";
    
    String RESTRICAO_TEXTO = "'P'";
    String RESTRICAO_ANEXO = "'D'";
    String TIPO_PROCESSO_DOCUMENTO_INTERNO_TEXTO = "getTipoProcessoDocumentoInternoIsModelo";
    String TIPO_PROCESSO_DOCUMENTO_INTERNO_ANEXO = "getTipoProcessoDocumentoInternoIsNotModelo";
    String TIPO_PROCESSO_DOCUMENTO_INTERNO_TEXTO_QUERY = DOCUMENTO_INTERNO_BASE_QUERY + RESTRICAO_TEXTO + DOCUMENTO_INTERNO_SUFIX;
    String TIPO_PROCESSO_DOCUMENTO_INTERNO_ANEXO_QUERY = DOCUMENTO_INTERNO_BASE_QUERY + RESTRICAO_ANEXO + DOCUMENTO_INTERNO_SUFIX;
}
