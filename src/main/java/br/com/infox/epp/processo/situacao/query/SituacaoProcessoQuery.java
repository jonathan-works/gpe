package br.com.infox.epp.processo.situacao.query;

public interface SituacaoProcessoQuery {
    
    String PARAM_ID_TASKINSTANCE = "idTaskInstance";
    String COUNT_TAREFAS_ATIVAS_BY_TASK_ID = "countTarefasAtivasByTaskId";
    String COUNT_TAREFAS_ATIVAS_BY_TASK_ID_QUERY = "select count(o.idTaskInstance) from SituacaoProcesso o "
            + "where o.idTaskInstance = :" + PARAM_ID_TASKINSTANCE;

}
