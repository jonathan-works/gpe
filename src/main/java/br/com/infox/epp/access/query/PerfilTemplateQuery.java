package br.com.infox.epp.access.query;

public interface PerfilTemplateQuery {
    
    String LIST_PERFIS_DENTRO_DE_ESTRUTURA = "Perfil.listPerfisDentroDeEstrutura";
    //TODO verificar o caso dos perfis de configuração onde a localização do perfilTemplate é nula
    String LIST_PERFIS_DENTRO_DE_ESTRUTURA_QUERY = "select o from PerfilTemplate o where o.localizacao is not null and o.ativo = true "
            + "order by o.descricao";

}
