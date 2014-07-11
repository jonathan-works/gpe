package br.com.infox.epp.access.query;

public interface EstruturaQuery {
    String TABLE_NAME = "tb_estrutura";
    String COLUMN_ID = "id_estrutura";
    String COLUMN_NOME = "nm_estrutura";
    String SEQUENCE_NAME = "sq_tb_estrutura";
    String PARAM_LOCALIZACAO = "localizacao";
    
    String ESTRUTURAS_DISPONIVEIS = "Estrutura.estruturasDisponiveis";
    String ESTRUTURAS_DISPONIVEIS_QUERY = "select o from Estrutura o where o.ativo = true and not exists "
            + "(select 1 from Localizacao l where l.estruturaFilho = o) "
            + "order by o.nome";
    
    String ESTRUTURAS_DISPONIVEIS_LOCALIZACAO = "Estrutura.estruturasDisponiveisLocalizacao";
    String ESTRUTURAS_DISPONIVEIS_LOCALIZACAO_QUERY = "select o from Estrutura o where o.ativo = true and not exists "
            + "(select 1 from Localizacao l where l.estruturaFilho = o and l != :" + PARAM_LOCALIZACAO + ") "
            + "order by o.nome";
}
