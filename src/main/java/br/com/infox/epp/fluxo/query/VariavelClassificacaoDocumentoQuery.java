package br.com.infox.epp.fluxo.query;


public interface VariavelClassificacaoDocumentoQuery {
    String PARAM_ID_FLUXO = "idFluxo";
    String PARAM_VARIAVEL = "variavel";
    String PARAM_VARIAVEIS = "variaveis";
    String PARAM_NOME_CLASSIFICACAO_DOCUMENTO = "nomeClassificacaoDocumento";
    String PARAM_CLASSIFICACAO_DOCUMENTO = "classificacaoDocumento";
    String PARAM_TIPO_DOCUMENTO = "tipoDocumento";
    
    String FIND_VARIAVEL_CLASSIFICACAO = "VariavelClassificacaoDocumento.existeVariavelClassificacao";
    String FIND_VARIAVEL_CLASSIFICACAO_QUERY = "select o from VariavelClassificacaoDocumento o where "
            + "o.fluxo.idFluxo = :" + PARAM_ID_FLUXO + " and o.variavel = :" + PARAM_VARIAVEL 
            + " and o.classificacaoDocumento = :" + PARAM_CLASSIFICACAO_DOCUMENTO;
    
    /**
     * Classificações associadas à variável e que não estão marcadas para remoção na publicação, ou seja, foram desassociadas na tela
     */
    String VARIAVEL_CLASSIFICACAO_LIST = "VariavelClassificacaoDocumento.variavelClassificacaoList";
    String VARIAVEL_CLASSIFICACAO_LIST_QUERY = "select o from VariavelClassificacaoDocumento o where o.fluxo.idFluxo = :" + PARAM_ID_FLUXO
            + " and o.classificacaoDocumento.sistema = false and o.removerNaPublicacao = false and o.variavel = :" + PARAM_VARIAVEL 
            + " order by o.classificacaoDocumento.descricao";
    
    /**
     * Classificações ativas e publicadas associadas à variável. Para uso no sistema.
     */
    String CLASSIFICACOES_PUBLICADAS_DA_VARIAVEL = "VariavelClassificacaoDocumento.classificacoesPublicadasDaVariavel";
    String CLASSIFICACOES_PUBLICADAS_DA_VARIAVEL_QUERY = "select o.classificacaoDocumento from VariavelClassificacaoDocumento o "
            + "where o.publicado = true and o.classificacaoDocumento.sistema = false and o.classificacaoDocumento.ativo = true and o.fluxo.idFluxo = :" + PARAM_ID_FLUXO 
            + " and o.variavel = :" + PARAM_VARIAVEL + " order by o.classificacaoDocumento.descricao";
    
    String PUBLICAR = "VariavelClassificacaoDocumento.publicar";
    String PUBLICAR_QUERY = "update VariavelClassificacaoDocumento o set o.publicado = true where o.fluxo.idFluxo = :" + PARAM_ID_FLUXO;
    
    /**
     * Remove as classificações associadas às variáveis que estão marcadas para serem removidas na publicação ou que se refiram a variáveis 
     * não existentes no fluxo
     */
    String REMOVER_CLASSIFICACOES_VARIAVEIS_OBSOLETAS_BASE_QUERY = "delete from VariavelClassificacaoDocumento o where "
            + "o.fluxo.idFluxo = :" + PARAM_ID_FLUXO
            + " and (o.removerNaPublicacao = true";
    String REMOVER_CLASSIFICACOES_VARIAVEIS_OBSOLETAS_VARIAVEIS_EXISTENTES_PART = " or o.variavel not in (:" + PARAM_VARIAVEIS + "))";
    
    /**
     * Classificações disponíveis para associação à variável
     */
    String CLASSIFICACOES_DISPONIVEIS_PARA_VARIAVEL_BASE_QUERY = "from ClassificacaoDocumento o where o.ativo = true and o.sistema = false and "
            + "o.inTipoDocumento in ('T', :" + PARAM_TIPO_DOCUMENTO + ") and "
            + "not exists (select 1 from VariavelClassificacaoDocumento v where v.classificacaoDocumento = o and "
            + "v.removerNaPublicacao = false and "
            + "v.variavel = :" + PARAM_VARIAVEL + " and v.fluxo.idFluxo = :" + PARAM_ID_FLUXO + ") ";
    
    String NOME_CLASSIFICACAO_FILTER = " and lower(o.descricao) like concat('%', lower(:" + PARAM_NOME_CLASSIFICACAO_DOCUMENTO + "), '%') ";
    
    String ORDER_BY_NOME_CLASSIFICACAO = " order by o.descricao";
    
    String TOTAL_CLASSIFICACOES_DISPONIVEIS_PARA_VARIAVEL_QUERY = "select count(o) " + CLASSIFICACOES_DISPONIVEIS_PARA_VARIAVEL_BASE_QUERY;
}
