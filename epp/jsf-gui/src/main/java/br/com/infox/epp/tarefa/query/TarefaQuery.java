package br.com.infox.epp.tarefa.query;

public interface TarefaQuery {

    String ID_JBPM_TASK_PARAM = "idJbpmTask";
    String TAREFA_BY_ID_JBPM_TASK = "tarefaByIdJbpmTask";
    String TAREFA_BY_ID_JBPM_TASK_QUERY = "select o from Tarefa o inner join o.tarefaJbpmList tJbpm where tJbpm.idJbpmTask = :"
            + ID_JBPM_TASK_PARAM;

    String TAREFA_PARAM = "tarefa";
    String FLUXO_PARAM = "fluxo";
    String TAREFA_BY_TAREFA_AND_FLUXO = "findTarefaByTarefaAndFluxo";
    String TAREFA_BY_TAREFA_AND_FLUXO_QUERY = "select t from Tarefa t where t.tarefa = :"
            + TAREFA_PARAM + " and t.fluxo.fluxo = :" + FLUXO_PARAM;

}
