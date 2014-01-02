package br.com.infox.epp.tarefa.query;

public interface TarefaQuery {

    String NOVAS_TAREFAS = "findNovasTarefas";
    String NOVAS_TAREFAS_QUERY = "insert into public.tb_tarefa (id_fluxo, ds_tarefa) "
            + "select f.id_fluxo, t.name_ from jbpm_task t "
            + "inner join jbpm_processdefinition pd on (pd.id_ = t.processdefinition_) "
            + "inner join public.tb_fluxo f on (f.ds_fluxo = pd.name_) "
            + "inner join jbpm_node jn on (t.tasknode_ = jn.id_ and jn.class_ = 'K') "
            + "where pd.id_ = t.processdefinition_ and not exists "
            + "(select 1 from public.tb_tarefa where ds_tarefa = t.name_ and id_fluxo = f.id_fluxo) "
            + "group by f.id_fluxo, t.name_";

    String PREVIOUS_NODES = "listPreviousNodes";
    String PREVIOUS_NODES_QUERY = "select max(nodeFrom.id_), nodeFrom.name_ from jbpm_transition t "
            + "inner join jbpm_node nodeFrom ON (nodeFrom.id_=t.from_) "
            + "inner join jbpm_task taskTo ON (taskTo.tasknode_=t.to_) "
            + "inner join tb_tarefa_jbpm tjTo ON (tjTo.id_jbpm_task=taskTo.id_) "
            + "where tjTo.id_tarefa=:idTarefa group by nodeFrom.name_";

}
