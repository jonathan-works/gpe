package br.com.infox.epp.processo.situacao.query;

public interface SituacaoProcessoQuery {

    String PARAM_ID_TASKINSTANCE = "idTaskInstance";
    String COUNT_TAREFAS_ATIVAS_BY_TASK_ID = "countTarefasAtivasByTaskId";
    String COUNT_TAREFAS_ATIVAS_BY_TASK_ID_QUERY = "select count(o.idTaskInstance) from SituacaoProcesso o "
            + "where o.idTaskInstance = :" + PARAM_ID_TASKINSTANCE;

    String TAREFAS_TREE_ROOTS = "tarefasTreeQueryRoots";
    String TAREFAS_TREE_QUERY_ROOTS_BASE = "select new map(s.nomeFluxo as nomeFluxo, max(s.idFluxo) as idFluxo, 'Fluxo' as type) "
            + "from SituacaoProcesso s where 1=1";
    String TAREFAS_TREE_QUERY_ROOTS_SUFIX = "group by s.nomeFluxo order by s.nomeFluxo";
    

    String TAREFAS_TREE_CHILDREN = "tarefasTreeQueryChildren";
    String TAREFAS_TREE_QUERY_CHILDREN_SUFIX = " group by s.nomeTarefa order by s.nomeTarefa";
    String TAREFAS_TREE_QUERY_CHILDREN_BASE = "select new map(s.nomeTarefa as nomeTarefa, "
            + "max(s.idTask) as idTask, max(s.idTarefa) as idTarefa, count(s.nomeCaixa) as qtdEmCaixa, "
            + "count(s.idProcesso) as qtd, 'caixa' as tree, 'Task' as type) from SituacaoProcesso s "
            + "where s.idFluxo = :idFluxo and s.pooledActor = :idPerfilTemplate";
    
    String TAREFAS_TREE_CAIXAS = "tarefasTreeQueryCaixas";
    String TAREFAS_TREE_QUERY_CAIXAS_BASE = "select new map(c.idCaixa as idCaixa, "
            + "c.tarefa.idTarefa as idTarefa, "
            + "c.nomeCaixa as nomeCaixa, "
            + "'Caixa' as type, "
            + "(select count(distinct s.idProcesso) from SituacaoProcesso s where s.idCaixa = c.idCaixa and s.pooledActor = :idPerfilTemplate";
    String TAREFAS_TREE_QUERY_CAIXAS_SUFIX = ") as qtd) from Caixa c where c.tarefa.idTarefa = :taskId order by c.nomeCaixa";

    String ID_TAREFA_PARAM = "idTarefa";

    String PROCESSOS_ABERTOS_BASE_QUERY = "select s.idProcesso from SituacaoProcesso s where s.idTarefa = :" + ID_TAREFA_PARAM;
    
    String COM_CAIXA_COND = " and s.idCaixa is not null";
    String SEM_CAIXA_COND = " and s.idCaixa is null";
    
    String GROUP_BY_PROCESSO_SUFIX = " group by s.idProcesso";
    
    String FILTRO_PREFIX = " and s.idProcesso IN (SELECT pe.idProcesso from ProcessoEpa pe LEFT JOIN pe.decisoraColegiada dc LEFT JOIN pe.decisoraMonocratica dm WHERE";
    String FILTRO_SUFIX = ") ";
    String AND = " and ";

    String COM_COLEGIADA = " dc = :colegiadaLogada";
    String COM_MONOCRATICA = " dm = :monocraticaLogada";
    String SEM_COLEGIADA = " dc is null";
    String SEM_MONOCRATICA = " dm is null";
    
    String PROCESSOS_COM_COLEGIADA_COND = FILTRO_PREFIX + COM_COLEGIADA + FILTRO_SUFIX;
    String PROCESSOS_COM_MONOCRATICA_COND = FILTRO_PREFIX + COM_MONOCRATICA + FILTRO_SUFIX;
    String PROCESSOS_COM_COLEGIADA_E_MONOCRATICA_COND = FILTRO_PREFIX + COM_COLEGIADA + AND + COM_MONOCRATICA + FILTRO_SUFIX;
    String PROCESSOS_SEM_COLEGIADA_NEM_MONOCRATICA_COND = FILTRO_PREFIX + SEM_COLEGIADA + AND + SEM_MONOCRATICA + FILTRO_SUFIX;

}
