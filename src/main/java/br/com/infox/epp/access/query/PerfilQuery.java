package br.com.infox.epp.access.query;

public interface PerfilQuery {
    
    String ID_PERFIL_PARAM = "idParam";
    String LOCALIZACAO_PARAM = "localizacao";
    String CAMINHO_COMPLETO_PARAM = "caminhoCompleto";
    String PAPEL_PARAM = "papel";
    String PAI_DA_ESTRUTURA_PARAM = "paiDaEstrutura";
    String EXISTE_PERFIL_BASE_QUERY = "select count(o) from Perfil o "
            + "where o.localizacao = :" + LOCALIZACAO_PARAM + " and o.papel = :" + PAPEL_PARAM + " and ";
    String SEM_ESTRUTURA = "o.paiDaEstrutura is null";
    String COM_ESTRUTURA = "o.paiDaEstrutura = :" + PAI_DA_ESTRUTURA_PARAM;
    String COM_ID = " and o.idPerfil <> :" + ID_PERFIL_PARAM;
    
    String EXISTE_PERFIL_COM_HIERARQUIA_LOCALIZACAO = "Perfil.existePerfilComLocalizacao";
    String EXISTE_PERFIL_COM_HIERARQUIA_LOCALIZACAO_QUERY = "select count(o) from Perfil o "
            + "where o.localizacao.caminhoCompleto like concat(:" + CAMINHO_COMPLETO_PARAM + ", '%')";
    
    String LIST_PERFIS_DENTRO_DE_ESTRUTURA = "Perfil.listPerfisDentroDeEstrutura";
    String LIST_PERFIS_DENTRO_DE_ESTRUTURA_QUERY = "select o from Perfil o where o.localizacao.estruturaPai is not null "
            + "order by o.descricao";
}
