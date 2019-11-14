package br.com.infox.epp.fluxo.query;


public interface RaiaPerfilQuery {
    String TABLE_NAME = "tb_raia_perfil";
    String COLUMN_ID = "id_raia_perfil";
    String COLUMN_RAIA = "nm_raia";
    String COLUMN_FLUXO = "id_fluxo";
    String COLUMN_PERFIL = "id_perfil_template";
    String SEQUENCE_NAME = "sq_tb_raia_perfil";
    
    String QUERY_PARAM_PERFIL = "perfil";
    String QUERY_PARAM_LOCALIZACAO = "localizacao";
    String QUERY_PARAM_FLUXO = "fluxo";
    String QUERY_PARAM_CD_FLUXO = "cdFluxo";
    String QUERY_PARAM_CAMINHO_COMPLETO = "caminhoCompleto";
    
    String LIST_BY_PERFIL = "RaiaPerfil.listByPerfil";
    String LIST_BY_PERFIL_QUERY = "select o from RaiaPerfil o where o.perfilTemplate = :" + QUERY_PARAM_PERFIL;
    
    String LIST_BY_LOCALIZACAO = "RaiaPerfil.listByLocalizacao";
    String LIST_BY_LOCALIZACAO_QUERY = "select o from RaiaPerfil o inner join o.perfilTemplate p "
            + "where p.localizacao = :" + QUERY_PARAM_LOCALIZACAO;
    
    String REMOVER_RAIAS_PERFIS_POR_FLUXO = "RaiaPerfil.removerRaiasPerfisPorFluxo";
    String REMOVER_RAIAS_PERFIS_POR_FLUXO_QUERY = "delete RaiaPerfil o where o.fluxo = :" + QUERY_PARAM_FLUXO;
    String LIST_BY_FLUXO = "RaiaPerfil.listByFluxo";
    String LIST_BY_FLUXO_QUERY = "select o from RaiaPerfil o where o.fluxo = :"+QUERY_PARAM_FLUXO;
}
