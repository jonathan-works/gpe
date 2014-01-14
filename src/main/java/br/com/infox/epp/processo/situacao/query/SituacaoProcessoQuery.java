package br.com.infox.epp.processo.situacao.query;

public interface SituacaoProcessoQuery {
    
    String PARAM_ID_TASKINSTANCE = "idTaskInstance";
    String COUNT_TAREFAS_ATIVAS_BY_TASK_ID = "countTarefasAtivasByTaskId";
    String COUNT_TAREFAS_ATIVAS_BY_TASK_ID_QUERY = "select count(o.idTaskInstance) from SituacaoProcesso o "
            + "where o.idTaskInstance = :" + PARAM_ID_TASKINSTANCE;
    
    String TAREFAS_TREE_ROOTS = "tarefasTreeQueryRoots";
    String TAREFAS_TREE_QUERY_ROOTS = "select new map(s.nomeFluxo as nomeFluxo, max(s.idFluxo) as idFluxo, 'Fluxo' as type) "
            + "from SituacaoProcesso s group by s.nomeFluxo order by s.nomeFluxo";
    
    String TAREFAS_TREE_CHILDREN = "tarefasTreeQueryChildren";
    String TAREFAS_TREE_QUERY_CHILDREN = "select new map("
            + "max(s.idSituacaoProcesso) as id, s.nomeTarefa as nomeTarefa, "
            + "max(s.idTask) as idTask, max(s.idTaskInstance) as idTaskInstance, "
            + "max(s.idTarefa) as idTarefa, count(s.nomeCaixa) as qtdEmCaixa, "
            + "count(s.idProcesso) as qtd, 'caixa' as tree, 'Task' as type) from SituacaoProcesso s "
            + "where s.idFluxo = :idFluxo group by s.nomeTarefa order by s.nomeTarefa";

}
