package br.com.infox.epp.access.query;

public interface EstruturaQuery {
	
    String TABLE_NAME = "tb_estrutura";
    String COLUMN_ID = "id_estrutura";
    String COLUMN_NOME = "nm_estrutura";
    String SEQUENCE_NAME = "sq_tb_estrutura";
    String PARAM_LOCALIZACAO = "localizacao";
    String PARAM_NOME = "nome";
    
    String ESTRUTURAS_DISPONIVEIS = "Estrutura.estruturasDisponiveis";
    String ESTRUTURAS_DISPONIVEIS_QUERY = "select o from Estrutura o where o.ativo = true order by o.nome";
    
    String ESTRUTURA_BY_NOME = "estruturaByNome";
    String ESTRUTURA_BY_NOME_QUERY = "select o from Estrutura o where o.nome = :" + PARAM_NOME;
}
