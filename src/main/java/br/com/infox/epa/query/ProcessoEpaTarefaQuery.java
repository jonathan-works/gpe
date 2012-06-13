package br.com.infox.epa.query;

public interface ProcessoEpaTarefaQuery {

	String QUERY_PARAM_TASKINSTANCE = "taskInstance";
	
	String GET_PROCESSO_EPA_TAREFA_BY_TASKINSTNACE = "getProcessoEpaTarefaByTaskInstance";
	
	String GET_PROCESSO_EPA_TAREFA_BY_TASKINSTNACE_QUERY = 
		"select o from ProcessoEpaTarefa o where o.taskInstance = :"+
		QUERY_PARAM_TASKINSTANCE;
	
	String LIST_ALL_NOT_ENDED = "listAllProcessoEpaTarefaNotEnded";
	
	String LIST_ALL_NOT_ENDED_QUERY = "select o from ProcessoEpaTarefa o where " +
								"o.dataFim is null";

	String QUERY_FORA_FLUXO = 
		 "select f.fluxo, p, t.tarefa, pt from ProcessoEpaTarefa pt "+
		 "inner join pt.tarefa t "+
		 "inner join pt.processoEpa p "+
		 "inner join p.naturezaCategoriaFluxo ncf "+
		 "inner join ncf.categoria c "+
		 "inner join ncf.fluxo f ";
	
}