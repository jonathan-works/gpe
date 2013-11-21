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
}
