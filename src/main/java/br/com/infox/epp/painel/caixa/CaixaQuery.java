package br.com.infox.epp.painel.caixa;

public interface CaixaQuery {
	
	String PARAM_ID_TAREFA = "idTarefa";
	String PARAM_ID_NODE_ANTERIOR = "idNodeAnterior";

	String CAIXA_BY_ID_TAREFA_AND_ID_NODE_ANTERIOR = "caixaByIdTarefaAndIdNodeAnterior";
	String CAIXA_BY_ID_TAREFA_AND_ID_NODE_ANTERIOR_QUERY = "select c From Caixa c "
			+ "where c.tarefa.idTarefa = :" + PARAM_ID_TAREFA
			+ " and c.idNodeAnterior = :" + PARAM_ID_NODE_ANTERIOR; 
}
