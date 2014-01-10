package br.com.infox.epp.documento.query;

public interface TipoProcessoDocumentoQuery {
    
    String ASSINATURA_OBRIGATORIA ="assinaturaObrigatoria";
    String ASSINATURA_OBRIGATORIA_QUERY = "select distinct tpdp.obrigatorio " +
            "from TipoProcessoDocumentoPapel tpdp " +
            "where tpdp.tipoProcessoDocumento=:" + 
            TipoProcessoDocumentoQuery.TIPO_PROCESSO_DOCUMENTO_PARAM +
            " and tpdp.papel=:" +
            TipoProcessoDocumentoQuery.PAPEL_PARAM;
    
    String TIPO_PROCESSO_DOCUMENTO_PARAM = "tpProcessoDoc";
    String PAPEL_PARAM = "papel";
    
    String RESTRICAO_DE_TIPO_PARAM = "restricaoDeTipo";
    String TIPO_PROCESSO_DOCUMENTO_INTERNO = "getTipoProcessoDocumentoInterno";
    String TIPO_PROCESSO_DOCUMENTO_INTERNO_QUERY = "select o from TipoProcessoDocumento o " +
            "where o.ativo = true and (o.visibilidade = 'I' OR o.visibilidade = 'A') and " +
            "(o.inTipoDocumento = :" + RESTRICAO_DE_TIPO_PARAM + " OR o.inTipoDocumento = 'T')";
}
