package br.com.infox.epp.documento.query;

public interface VinculoClassificacaoTipoDocumentoQuery {

    String PARAM_CLASSIFICACAO_DOCUMENTO = "classificacaoDocumento";
    String PARAM_TIPO_MODELO_DOCUMENTO = "tipoModeloDocumento";

    String FIND_BY_TIPO_CLASSIFICACAO = "VinculoClassificacaoTipoDocumento.findByTipoClassificacao";
    String FIND_BY_TIPO_CLASSIFICACAO_QUERY = "select ent from VinculoClassificacaoTipoDocumento ent"
            + " where ent.tipoModeloDocumento=:" + PARAM_TIPO_MODELO_DOCUMENTO + " and ent.classificacaoDocumento=:"
            + PARAM_CLASSIFICACAO_DOCUMENTO;
    
    String FIND_BY_CLASSIFICACAO = "VinculoClassificacaoTipoDocumento.findByClassificacao";
    String FIND_BY_CLASSIFICACAO_QUERY = "select ent from VinculoClassificacaoTipoDocumento ent inner join fetch ent.tipoModeloDocumento tmd "
    		+ " where ent.classificacaoDocumento=:"
    		+ PARAM_CLASSIFICACAO_DOCUMENTO;

    String GET_MODELO_DOCUMENTO_LIST_BY_CLASSIFICACAO = "VinculoClassificacaoTipoDocumento.getModeloDocumentoListByClassificacao";
    String GET_MODELO_DOCUMENTO_LIST_BY_CLASSIFICACAO_QUERY = "select m from VinculoClassificacaoTipoDocumento vinc "
            + "inner join vinc.tipoModeloDocumento tipo " + "inner join tipo.modeloDocumentoList m "
            + "where ent.classificacaoDocumento=:" + PARAM_CLASSIFICACAO_DOCUMENTO;
}
