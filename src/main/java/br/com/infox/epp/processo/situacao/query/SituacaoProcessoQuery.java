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

//    String PROCESSOS_ABERTOS = "processosAbertos";
//    String PROCESSOS_ABERTOS_QUERY = "select s.idProcesso from SituacaoProcesso s "
//            + "where s.idTarefa = :"
//            + ID_TAREFA_PARAM + ""
//            + " group by s.idProcesso";
//
//    String PROCESSOS_ABERTOS_SEM_CAIXA = "processosAbertosQueNaoEstaoEmNenhumaCaixa";
//    String PROCESSOS_ABERTOS_SEM_CAIXA_QUERY = "select s.idProcesso from SituacaoProcesso s "
//            + "where s.idTarefa = :"
//            + ID_TAREFA_PARAM
//            + " and s.idCaixa is null group by s.idProcesso";
//
//    String PROCESSOS_ABERTOS_EM_CAIXA = "processosAbertosEmCaixa";
//    String PROCESSOS_ABERTOS_EM_CAIXA_QUERY = "select s.idProcesso from SituacaoProcesso s "
//            + "where s.idTarefa = :" + ID_TAREFA_PARAM
//            + " and s.idCaixa is not null group by s.idProcesso";
    
    String PROCESSOS_ABERTOS_BASE_QUERY = "select s.idProcesso from SituacaoProcesso s where s.idTarefa = :" + ID_TAREFA_PARAM;
    
    String COM_CAIXA_COND = " and s.idCaixa is not null";
    String SEM_CAIXA_COND = " and s.idCaixa is null";
    
    String GROUP_BY_PROCESSO_SUFIX = " group by s.idProcesso";
    
    String FILTRO_PREFIX = " and s.idProcesso IN (SELECT pe.idProcesso from ProcessoEpa pe WHERE";
    String FILTRO_SUFIX = ") ";
    String AND = " and ";

    String COM_COLEGIADA = " pe.decisoraColegiada = :colegiadaLogada";
    String COM_MONOCRATICA = " pe.decisoraMonocratica = :monocraticaLogada";
    String SEM_COLEGIADA = " pe.decisoraColegiada is null";
    String SEM_MONOCRATICA = " pe.decisoraMonocratica is null";
    
    String PROCESSOS_COM_COLEGIADA_COND = FILTRO_PREFIX + COM_COLEGIADA + AND + SEM_MONOCRATICA + FILTRO_SUFIX;
    String PROCESSOS_COM_MONOCRATICA_COND = FILTRO_PREFIX + SEM_COLEGIADA + AND + COM_MONOCRATICA + FILTRO_SUFIX;
    String PROCESSOS_COM_COLEGIADA_E_MONOCRATICA_COND = FILTRO_PREFIX + COM_COLEGIADA + AND + COM_MONOCRATICA + FILTRO_SUFIX;
    String PROCESSOS_SEM_COLEGIADA_NEM_MONOCRATICA_COND = FILTRO_PREFIX + SEM_COLEGIADA + AND + SEM_MONOCRATICA + FILTRO_SUFIX;
    

}
