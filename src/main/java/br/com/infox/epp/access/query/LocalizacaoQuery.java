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
    
    String LOCALIZACOES_ESTRUTURA = "localizacoesEstrutura";
    String LOCALIZACOES_ESTRUTURA_QUERY = "select o from Localizacao o where o.estrutura = true order by o.localizacao";
    

}
