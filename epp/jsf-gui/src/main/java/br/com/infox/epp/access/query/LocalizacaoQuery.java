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
    String CAMINHO_COMPLETO = "ds_caminho_completo";
    String LOCALIZACAO_ATTRIBUTE = "localizacao";
    String LOCALIZACAO_PAI_ATTRIBUTE = "localizacaoPai";

    String QUERY_PARAM_ID_LOCALIZACAO = "idLocalizacao";
    String QUERY_PARAM_ESTRUTURA_PAI = "estruturaPai";
    String QUERY_PARAM_CAMINHO_COMPLETO = "caminhoCompleto";
    String QUERY_PARAM_LOCALIZACAO = "localizacao";

    String LOCALIZACOES_BY_IDS = "Localizacao.localizacoesByIds";
    String LOCALIZACOES_BY_IDS_QUERY = "select o from Localizacao o where o.idLocalizacao in :"
            + QUERY_PARAM_ID_LOCALIZACAO;
    
    String QUERY_PARAM_CODIGO = "codigo";
    String LOCALIZACAO_BY_CODIGO = "Localizacao.localizacaoByCodigo";
    String LOCALIZACAO_BY_CODIGO_QUERY = "select o from Localizacao o where o.codigo = :" + QUERY_PARAM_CODIGO;
    
    String LIST_BY_NOME_ESTRUTURA_PAI = "listByNomeEstruturaPai";
    String LIST_BY_NOME_ESTRUTURA_PAI_QUERY = "select o from Localizacao o where o.estruturaPai.nome = :"
            + ESTRUTURA_PAI;

    String IS_LOCALIZACAO_ANCESTOR = "isLocalizacaoAncestor";
    String IS_LOCALIZACAO_ANCESTOR_QUERY = "select distinct 1 from Localizacao o where o.caminhoCompleto like concat(:"
            + CAMINHO_COMPLETO + ",'%')" + " and o = :" + LOCALIZACAO_ATTRIBUTE;
    
    String IS_CAMINHO_COMPLETO_DUPLICADO_QUERY = "select count(o) from Localizacao o where o.estruturaPai is null and "
            + " o.caminhoCompleto = :" + QUERY_PARAM_CAMINHO_COMPLETO;
    
    String IS_CAMINHO_COMPLETO_DUPLICADO_DENTRO_ESTRUTURA_QUERY = "select count(o) from Localizacao o "
            + " where o.estruturaPai = :" + QUERY_PARAM_ESTRUTURA_PAI + " and "
            + " o.caminhoCompleto = :" + QUERY_PARAM_CAMINHO_COMPLETO;
    
    String PART_FILTER_BY_LOCALIZACAO = " and o.idLocalizacao <> :" + QUERY_PARAM_ID_LOCALIZACAO;
    
    String LOCALIZACAO_DENTRO_ESTRUTURA = "Localizacao.localizacaoDentroEstrutura";
    String LOCALIZACAO_DENTRO_ESTRUTURA_QUERY = "select o from Localizacao o where o.estruturaPai is not null and o.localizacao = :" + QUERY_PARAM_LOCALIZACAO;
    
    String LOCALIZACAO_FORA_ESTRUTURA_BY_NOME = "Localizacao.localizacaoForaEstrutura";
    String LOCALIZACAO_FORA_ESTRUTURA_BY_NOME_QUERY = "select o from Localizacao o where o.estruturaFilho is null and o.localizacao = :" + QUERY_PARAM_LOCALIZACAO;
    
    String ESTRUTURA_FILHO_PARAM = "estruturaFilho";
    String LOCALIZACOES_BY_ESTRUTURA_FILHO = "Localizacao.localizacaoByEstrutura";
    String LOCALIZACOES_BY_ESTRUTURA_FILHO_QUERY = "select o from Localizacao o where o.estruturaFilho = :" + ESTRUTURA_FILHO_PARAM;
    
    String LOCALIZACAO_BY_NOME = "Localizacao.localizacaoByNome";
    String LOCALIZACAO_BY_NOME_QUERY = "select o from Localizacao o where o.localizacao = :" + QUERY_PARAM_LOCALIZACAO;
    
    String USOS_DA_HIERARQUIA_LOCALIZACAO = "Localizacao.usosHierarquiaLocalizacao";
    String USOS_DA_HIERARQUIA_LOCALIZACAO_QUERY = 
        "SELECT tipo FROM " +
        "(SELECT 'P' AS tipo FROM tb_perfil_template p " +
        "INNER JOIN tb_localizacao l ON (p.id_localizacao = l.id_localizacao) " +
        "WHERE l.ds_caminho_completo like concat(:" + QUERY_PARAM_CAMINHO_COMPLETO + ", '%')" +
        
 		"UNION " +
 		
		"SELECT 'P' AS tipo FROM tb_usuario_perfil p " +
		"INNER JOIN tb_localizacao l ON (p.id_localizacao = l.id_localizacao) " +
		"WHERE l.ds_caminho_completo like concat(:" + QUERY_PARAM_CAMINHO_COMPLETO + ", '%')" +
        
        "UNION " +
        
        "SELECT 'P' AS tipo FROM tb_usuario_perfil up " +
        "INNER JOIN tb_perfil_template p ON (p.id_perfil_template=up.id_perfil_template) " +
        "INNER JOIN tb_localizacao l ON (p.id_localizacao = l.id_localizacao) " +
        "INNER JOIN tb_localizacao lp ON (up.id_localizacao = lp.id_localizacao) " +
        "WHERE l.id_estrutura_pai IS NOT NULL " +
        "AND lp.ds_caminho_completo like concat(:" + QUERY_PARAM_CAMINHO_COMPLETO + ", '%')" + 
        
        "UNION " +
        
        "SELECT 'RP' AS tipo FROM tb_raia_perfil rp " +
        "INNER JOIN tb_perfil_template p ON (p.id_perfil_template = rp.id_perfil_template) " +
        "INNER JOIN tb_localizacao l ON (p.id_localizacao = l.id_localizacao) " +
        "WHERE l.ds_caminho_completo like concat(:" + QUERY_PARAM_CAMINHO_COMPLETO + ", '%')" + 
        
        "UNION " +
        
        "SELECT 'RP' AS tipo FROM tb_raia_perfil rp " +
        "INNER JOIN tb_perfil_template p ON (p.id_perfil_template = rp.id_perfil_template) " +
        "INNER JOIN tb_usuario_perfil up ON (up.id_perfil_template=p.id_perfil_template) " +
        "INNER JOIN tb_localizacao l ON (p.id_localizacao = l.id_localizacao) " +
        "INNER JOIN tb_localizacao lp ON (up.id_localizacao = lp.id_localizacao) " +
        "WHERE l.id_estrutura_pai IS NOT NULL " +
        "AND lp.ds_caminho_completo like concat(:" + QUERY_PARAM_CAMINHO_COMPLETO + ", '%')" + 
        
        ") a ";
}