package br.com.infox.epp.tarefa.query;

public interface TarefaQuery {

    String NOVAS_TAREFAS = "findNovasTarefas";
    String NOVAS_TAREFAS_QUERY = "insert into tb_tarefa (id_fluxo, ds_tarefa) "
            + "select f.id_fluxo, t.name_ from jbpm_task t "
            + "inner join jbpm_processdefinition pd on (pd.id_ = t.processdefinition_) "
            + "inner join tb_fluxo f on (f.ds_fluxo = pd.name_) "
            + "inner join jbpm_node jn on (t.tasknode_ = jn.id_ and jn.class_ = 'K') "
            + "where pd.id_ = t.processdefinition_ and not exists "
            + "(select 1 from tb_tarefa where ds_tarefa = t.name_ and id_fluxo = f.id_fluxo) "
            + "group by f.id_fluxo, t.name_";

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
