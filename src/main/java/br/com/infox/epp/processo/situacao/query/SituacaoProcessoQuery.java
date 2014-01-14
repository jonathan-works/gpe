package br.com.infox.epp.processo.situacao.query;

public interface SituacaoProcessoQuery {
    
    String PARAM_ID_TASKINSTANCE = "idTaskInstance";
    String COUNT_TAREFAS_ATIVAS_BY_TASK_ID = "countTarefasAtivasByTaskId";
    String COUNT_TAREFAS_ATIVAS_BY_TASK_ID_QUERY = "select count(o.idTaskInstance) from SituacaoProcesso o "
            + "where o.idTaskInstance = :" + PARAM_ID_TASKINSTANCE;
    
    String TAREFAS_TREE_ROOTS = "tarefasTreeQueryRoots";
    String TAREFAS_TREE_QUERY_ROOTS = "select new map(s.nomeFluxo as nomeFluxo, max(s.idFluxo) as idFluxo, 'Fluxo' as type) "
            + "from SituacaoProcesso s group by s.nomeFluxo order by s.nomeFluxo";

}
