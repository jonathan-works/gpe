package br.com.infox.ibpm.query;

public interface ProcessoLocalizacaoIbpmQuery {

	String QUERY_PARAM_ID_TASK_INSTANCE = "idTaskInstance";
	String QUERY_PARAM_PROCESSO = "processo";
	String QUERY_PARAM_LOCALIZACAO = "localizacao";
	String QUERY_PARAM_PAPEL = "papel";
	String QUERY_PARAM_ID_TASK = "idTask";
	
	String LIST_BY_TASK_INSTANCE = "listProcessoLocalizacaoIbpmByTaskInstance";
	String LIST_BY_TASK_INSTANCE_QUERY = 
		"select o.localizacao from ProcessoLocalizacaoIbpm o where " +
		"o.idTaskInstance = :"+QUERY_PARAM_ID_TASK_INSTANCE+" and " +
		"o.contabilizar = true";
	
	String LIST_ID_TASK_INSTANCE_BY_LOCALIZACAO_PAPEL = "listIdTaskInstanceByLocalizacaoPapel";
	String LIST_ID_TASK_INSTANCE_BY_LOCALIZACAO_PAPEL_QUERY = "select distinct o.idTaskInstance from ProcessoLocalizacaoIbpm o" +
			                                           " where o.processo = :processo" +
			                                           " and o.localizacao = :localizacao" +
			                                           " and o.papel = :papel";
	
	String LIST_ID_TASK_INSTANCE_BY_ID_TAREFA = "listIdTaskInstanceByIdTarefa";
	String LIST_ID_TASK_INSTANCE_BY_ID_TAREFA_QUERY = "select o.idTaskInstance from SituacaoProcesso o" +
														" where o.idTarefa = :idTask " +
														"and exists (from ProcessoLocalizacaoIbpm tl " +
															"where tl.processo = :processo " +
															"and tl.idTaskInstance = o.idTaskInstance " +
															"and tl.localizacao = :localizacao " +
															"and (tl.papel = :papel or tl.papel is null))";
	
}