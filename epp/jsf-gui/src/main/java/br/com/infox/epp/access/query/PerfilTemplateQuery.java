package br.com.infox.epp.access.query;

public interface PerfilTemplateQuery {

    String PARAM_LOCALIZACAO = "localizacao";
    String PARAM_PAPEL = "papel";
    String PARAM_DESCRICAO = "descricao";

    String LIST_PERFIS_DENTRO_DE_ESTRUTURA = "Perfil.listPerfisDentroDeEstrutura";
    // TODO verificar o caso dos perfis de configuração onde a localização do
    // perfilTemplate é nula
    String LIST_PERFIS_DENTRO_DE_ESTRUTURA_QUERY = "select o from PerfilTemplate o where o.localizacao is not null and o.ativo = true "
            + "order by o.descricao";
    String GET_BY_LOCALIZACAO_PAPEL = "PerfilTemplate.getByLocalizacaoPapel";
    String GET_BY_LOCALIZACAO_PAPEL_QUERY = "select o from PerfilTemplate o where o.localizacao = :"
            + PARAM_LOCALIZACAO + " and o.papel =:" + PARAM_PAPEL;
    
    String GET_BY_DESCRICAO = "getPerfilteTemplateByDescricao";
    String GET_BY_DESCRICAO_QUERY = "select o from PerfilTemplate o where o.descricao = :" + PARAM_DESCRICAO;
    
    String GET_BY_LOCALIZACAO_PAI_DESCRICAO = "getPerfilTemplateByLocalizacaoPaiAndDescricao";
    String GET_BY_LOCALIZACAO_PAI_DESCRICAO_QUERY = "select o from PerfilTemplate o where exists (select 1 from Localizacao l "
    		+ "inner join l.estruturaFilho e inner join e.localizacoes lf "
    		+ "where lf.idLocalizacao = o.localizacao.idLocalizacao and l.idLocalizacao = :" + PARAM_LOCALIZACAO + " ) "
    		+ "and o.descricao = :" + PARAM_DESCRICAO;

}
