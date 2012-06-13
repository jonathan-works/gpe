package br.com.infox.ibpm.query;

public interface ProcessoLocalizacaoIbpmQuery {

	String QUERY_PARAM_ID_TASK_INSTANCE = "idTaskInstance";
	
	String LIST_BY_TASK_INSTANCE = "listProcessoLocalizacaoIbpmByTaskInstance";
	String LIST_BY_TASK_INSTANCE_QUERY = 
		"select o.localizacao from ProcessoLocalizacaoIbpm o where " +
		"o.idTaskInstance = :"+QUERY_PARAM_ID_TASK_INSTANCE+" and " +
		"o.contabilizar = true";
	
}