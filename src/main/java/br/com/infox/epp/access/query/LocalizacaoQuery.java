package br.com.infox.epp.access.query;

public interface LocalizacaoQuery {

    String TABLE_LOCALIZACAO = "tb_localizacao";
    String SEQUENCE_LOCALIZACAO = "public.sq_tb_localizacao";
    String ID_LOCALIZACAO = "id_localizacao";
    String LOCALIZACAO_PAI = "id_localizacao_pai";
    String DESCRICAO_LOCALIZACAO = "ds_localizacao";
    String IN_ESTRUTURA = "in_estrutura";
    String ESTRUTURA = "id_estrutura";
    String TWITTER = "in_twitter";
    String CAMINHO_COMPLETO = "ds_caminho_completo";
    String LOCALIZACAO_ATTRIBUTE = "localizacao";
    String LOCALIZACAO_PAI_ATTRIBUTE = "localizacaoPai";

    String QUERY_PARAM_ID_LOCALIZACAO = "idLocalizacao";

    String LOCALIZACOES_ESTRUTURA = "localizacoesEstrutura";
    String LOCALIZACOES_ESTRUTURA_QUERY = "select o from Localizacao o where o.estrutura = true and o.caminhoCompleto like concat(:"
            + CAMINHO_COMPLETO + ",'%') order by o.localizacao";

    String LOCALIZACOES_BY_IDS = "Localizacao.localizacoesByIds";
    String LOCALIZACOES_BY_IDS_QUERY = "select o from Localizacao o where o.idLocalizacao in :"
            + QUERY_PARAM_ID_LOCALIZACAO;

    String IS_LOCALIZACAO_ANCESTOR = "isLocalizacaoAncestor";
    String IS_LOCALIZACAO_ANCESTOR_QUERY = "select distinct 1 from Localizacao o where o.caminhoCompleto like concat(:"
            + CAMINHO_COMPLETO + ",'%')" + " and o = :" + LOCALIZACAO_ATTRIBUTE;

}
