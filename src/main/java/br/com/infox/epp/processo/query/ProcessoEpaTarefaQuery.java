package br.com.infox.epp.processo.query;

public interface ProcessoEpaTarefaQuery {

	String QUERY_PARAM_TASKINSTANCE = "taskInstance";
	String QUERY_PARAM_TIPO_PRAZO = "tipoPrazo";
	
	String GET_PROCESSO_EPA_TAREFA_BY_TASKINSTNACE = "getProcessoEpaTarefaByTaskInstance";
	
	String GET_PROCESSO_EPA_TAREFA_BY_TASKINSTNACE_QUERY = 
		"select o from ProcessoEpaTarefa o where o.taskInstance = :"+
		QUERY_PARAM_TASKINSTANCE;
	
	String TAREFA_NOT_ENDED_BY_TIPO_PRAZO = "listAllProcessoEpaTarefaNotEnded";
	
	String TAREFA_NOT_ENDED_BY_TIPO_PRAZO_QUERY = "select o from ProcessoEpaTarefa o " +
									  			  "where o.dataFim is null and o.tarefa.tipoPrazo = :" + QUERY_PARAM_TIPO_PRAZO;
	
	String TAREFA_ENDED = "listAllProcessoEppTarefaEnded";
	String TAREFA_ENDED_QUERY = "select pet from ProcessoEpaTarefa pet " +
								"where not pet.dataFim is null";

	String QUERY_FORA_FLUXO = 
		 "select f.fluxo, p, t.tarefa, pt from ProcessoEpaTarefa pt "+
		 "inner join pt.tarefa t "+
		 "inner join pt.processoEpa p "+
		 "inner join p.naturezaCategoriaFluxo ncf "+
		 "inner join ncf.categoria c "+
		 "inner join ncf.fluxo f ";
	
}