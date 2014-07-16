package br.com.infox.epp.access.query;

public interface LocalizacaoQuery {

    String TABLE_LOCALIZACAO = "tb_localizacao";
    String SEQUENCE_LOCALIZACAO = "sq_tb_localizacao";
    String ID_LOCALIZACAO = "id_localizacao";
    String LOCALIZACAO_PAI = "id_localizacao_pai";
    String DESCRICAO_LOCALIZACAO = "ds_localizacao";
    String IN_ESTRUTURA = "in_estrutura";
    String ESTRUTURA_FILHO = "id_estrutura_filho";
    String ESTRUTURA_PAI = "id_estrutura_pai";
    String TWITTER = "in_twitter";
    String LOCALIZACAO_RAIZ_ESTRUTURA = "in_loc_raiz_estrutura";
    String CAMINHO_COMPLETO = "ds_caminho_completo";
    String LOCALIZACAO_ATTRIBUTE = "localizacao";
    String LOCALIZACAO_PAI_ATTRIBUTE = "localizacaoPai";

    String QUERY_PARAM_ID_LOCALIZACAO = "idLocalizacao";
    String QUERY_PARAM_ESTRUTURA_PAI = "estruturaPai";
    String QUERY_PARAM_CAMINHO_COMPLETO = "caminhoCompleto";

    String LOCALIZACOES_BY_IDS = "Localizacao.localizacoesByIds";
    String LOCALIZACOES_BY_IDS_QUERY = "select o from Localizacao o where o.idLocalizacao in :"
            + QUERY_PARAM_ID_LOCALIZACAO;

    String IS_LOCALIZACAO_ANCESTOR = "isLocalizacaoAncestor";
    String IS_LOCALIZACAO_ANCESTOR_QUERY = "select distinct 1 from Localizacao o where o.caminhoCompleto like concat(:"
            + CAMINHO_COMPLETO + ",'%')" + " and o = :" + LOCALIZACAO_ATTRIBUTE;
    
    String ATUALIZAR_ESTRUTURA_PAI = "Localizacao.atualizarEstruturaPai";
    String QUERY_ATUALIZAR_ESTRUTURA_PAI = "update Localizacao o set o.estruturaPai = :" + QUERY_PARAM_ESTRUTURA_PAI +
            " where o.caminhoCompleto like concat(:" + QUERY_PARAM_CAMINHO_COMPLETO + ",'%')";
    
    String REMOVER_ESTRUTURA_PAI = "Localizacao.removerEstruturaPai";
    String QUERY_REMOVER_ESTRUTURA_PAI = "update Localizacao o set o.estruturaPai = null, o.localizacaoRaizEstrutura = false "
            + "where o.caminhoCompleto like concat(:" + QUERY_PARAM_CAMINHO_COMPLETO + ",'%')";

    String EXISTE_LOCALIZACAO_FILHA_COM_ESTRUTURA_PAI_DIFERENTE = "Localizacao.existeLocalizacaoFilhaComEstruturaPaiDiferente";
    String QUERY_EXISTE_LOCALIZACAO_FILHA_COM_ESTRUTURA_PAI_DIFERENTE = "select count(l.idLocalizacao) from Localizacao l where "
            + "l.caminhoCompleto like concat(:" + QUERY_PARAM_CAMINHO_COMPLETO + ",'%') and l.idLocalizacao != :" + QUERY_PARAM_ID_LOCALIZACAO
            + " and l.estruturaPai is not null and l.estruturaPai != :" + QUERY_PARAM_ESTRUTURA_PAI;
    
    String EXISTE_LOCALIZACAO_FILHA_COM_ESTRUTURA_FILHO = "Localizacao.existeLocalizacaoFilhaComEstruturaFilho";
    String QUERY_EXISTE_LOCALIZACAO_FILHA_COM_ESTRUTURA_FILHO = "select count(l.idLocalizacao) from Localizacao l where "
            + "l.caminhoCompleto like concat(:" + QUERY_PARAM_CAMINHO_COMPLETO + ",'%') and l.idLocalizacao != :" + QUERY_PARAM_ID_LOCALIZACAO
            + " and l.estruturaFilho is not null";
}