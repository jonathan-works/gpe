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
    
    String TAREFAS_TREE_CAIXAS = "tarefasTreeQueryCaixas";
    String TAREFAS_TREE_QUERY_CAIXAS = "select new map(c.idCaixa as idCaixa, "
            + "c.tarefa.idTarefa as idTarefa, "
            + "c.nomeCaixa as nomeCaixa, "
            + "'Caixa' as type, "
            + "(select count(distinct sp.idProcesso) from SituacaoProcesso sp where sp.idCaixa = c.idCaixa) as qtd) "
                + "from Caixa c where c.tarefa.idTarefa = :taskId order by c.nomeCaixa";
    
    String ID_TAREFA_PARAM = "idTarefa";
    
    String PROCESSOS_ABERTOS = "processosAbertos";
    String PROCESSOS_ABERTOS_QUERY = "select s.idProcesso from SituacaoProcesso s "
            + "where s.idTarefa = :" + ID_TAREFA_PARAM + " group by s.idProcesso";
    
    String PROCESSOS_ABERTOS_SEM_CAIXA = "processosAbertosQueNaoEstaoEmNenhumaCaixa";
    String PROCESSOS_ABERTOS_SEM_CAIXA_QUERY = "select s.idProcesso from SituacaoProcesso s "
            + "where s.idTarefa = :" + ID_TAREFA_PARAM + " and s.idCaixa is null group by s.idProcesso";
    
    String PROCESSOS_ABERTOS_EM_CAIXA = "processosAbertosEmCaixa";
    String PROCESSOS_ABERTOS_EM_CAIXA_QUERY = "select s.idProcesso from SituacaoProcesso s "
            + "where s.idTarefa = :" + ID_TAREFA_PARAM + " and s.idCaixa is not null group by s.idProcesso";

}
