package br.com.infox.ibpm.variable.query;

public interface DominioVariavelTarefaQuery {
	String TABLE_NAME = "tb_dominio_variavel_tarefa";
	String SEQUENCE_NAME = "sq_tb_dominio_variavel_tarefa";
	String COLUMN_ID = "id_dominio_variavel_tarefa";
	String COLUMN_DOMINIO = "ds_dominio_variavel_tarefa";
	String COLUMN_NOME = "nm_dominio_variavel_tarefa";
	String PARAM_ID = "id";
	
	String NAMED_QUERY_GET_DOMINIO = "DominioVariavelTarefa.getDominio";
	String QUERY_GET_DOMINIO = "select o from DominioVariavelTarefa o where o.id = :" + PARAM_ID;
}
