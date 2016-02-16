package br.com.infox.epp.processo.localizacao.query;

public interface ProcessoLocalizacaoIbpmQuery {

    String PARAM_ID_TASK_INSTANCE = "idTaskInstance";
    String PARAM_PROCESSO = "processo";
    String PARAM_LOCALIZACAO = "localizacao";
    String PARAM_PAPEL = "papel";
    String PARAM_ID_TASK = "idTask";
    String PARAM_TASK_INSTANCE = "taskInstance";

    String LIST_BY_TASK_INSTANCE = "listProcessoLocalizacaoIbpmByTaskInstance";
    String LIST_BY_TASK_INSTANCE_QUERY = "select o.localizacao from ProcessoLocalizacaoIbpm o where "
            + "o.idTaskInstance = :"
            + PARAM_ID_TASK_INSTANCE
            + " and "
            + "o.contabilizar = true";

    String LIST_ID_TASK_INSTANCE_BY_LOCALIZACAO_PAPEL = "listIdTaskInstanceByLocalizacaoPapel";
    String LIST_ID_TASK_INSTANCE_BY_LOCALIZACAO_PAPEL_QUERY = "select distinct o.idTaskInstance from ProcessoLocalizacaoIbpm o"
            + " where o.processo = :processo"
            + " and o.localizacao = :localizacao" + " and o.papel = :papel";

    String COUNT_PROCESSO_LOCALIZACAO_IBPM_BY_ATTRIBUTES = "countProcessoLocalizacaoIbpmByAttributes";
    String COUNT_PROCESSO_LOC_IBPM_BY_IDP_LOC_AND_PAPEL_QUERY = "select count(o) from ProcessoLocalizacaoIbpm o "
            + "where o.processo = :"
            + PARAM_PROCESSO
            + " and o.localizacao = :"
            + PARAM_LOCALIZACAO
            + " and o.papel = :"
            + PARAM_PAPEL;

    String PARAM_PROCESS_ID = "processId";
    String PARAM_TASK_ID = "taskId";
    String DELETE_BY_PROCESS_ID_AND_TASK_ID = "deleteProcessoLocalizacaoIbpmByProcessAndTask";
    String DELETE_BY_PROCESS_ID_AND_TASK_ID_QUERY = "delete from ProcessoLocalizacaoIbpm o "
            + "where o.idProcessInstanceJbpm = :"
            + PARAM_PROCESS_ID
            + " and o.idTaskJbpm = :" + PARAM_TASK_ID;
    
    String DELETE_BY_TASK_INSTANCE_ID = "deleteProcessoLocalizacaoIbpmByTaskInstance";
    String DELETE_BY_TASK_INSTANCE_ID_QUERY = "delete from ProcessoLocalizacaoIbpm o "
            + "where o.idTaskInstance = :" + PARAM_TASK_INSTANCE;

}
