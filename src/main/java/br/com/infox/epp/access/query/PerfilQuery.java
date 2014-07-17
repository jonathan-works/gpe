package br.com.infox.epp.access.query;

public interface PerfilQuery {
    
    String LOCALIZACAO_PARAM = "localizacao";
    String PAPEL_PARAM = "papel";
    String PAI_DA_ESTRUTURA_PARAM = "paiDaEstrutura";
    String EXISTE_PERFIL_BASE_QUERY = "select count(o) from Perfil o "
            + "where o.localizacao = :" + LOCALIZACAO_PARAM + " and o.papel = :" + PAPEL_PARAM + " and ";
    String SEM_ESTRUTURA = "o.paiDaEstrutura is null";
    String COM_ESTRUTURA = "o.paiDaEstrutura = :" + PAI_DA_ESTRUTURA_PARAM;

}
