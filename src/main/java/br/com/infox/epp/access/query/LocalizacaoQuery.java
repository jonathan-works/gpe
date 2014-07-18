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
    
    String IS_CAMINHO_COMPLETO_DUPLICADO_QUERY = "select count(o) from Localizacao o where o.estruturaPai is null and "
            + " o.caminhoCompleto = :" + QUERY_PARAM_CAMINHO_COMPLETO;
    
    String IS_CAMINHO_COMPLETO_DUPLICADO_DENTRO_ESTRUTURA_QUERY = "select count(o) from Localizacao o "
            + " where o.estruturaPai = :" + QUERY_PARAM_ESTRUTURA_PAI + " and "
            + " o.caminhoCompleto = :" + QUERY_PARAM_CAMINHO_COMPLETO;
    
    String PART_FILTER_BY_LOCALIZACAO = " and o.idLocalizacao <> :" + QUERY_PARAM_ID_LOCALIZACAO;
}