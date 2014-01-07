package br.com.infox.epp.tarefa.query;

public interface TarefaJbpmQuery {

    String INSERT_TAREFA_VERSIONS = "insertTarefaVersions";
    String INSERT_TAREFA_VERSIONS_QUERY = "insert into public.tb_tarefa_jbpm (id_tarefa, id_jbpm_task) "
            + "select t.id_tarefa, jt.id_ from public.tb_tarefa t "
            + "inner join public.tb_fluxo f using (id_fluxo) "
            + "inner join jbpm_task jt on jt.name_ = t.ds_tarefa "
            + "inner join jbpm_processdefinition pd on pd.id_ = jt.processdefinition_ "
            + "where f.ds_fluxo = pd.name_ and not exists "
            + "(select 1 from public.tb_tarefa_jbpm tj "
            + "where tj.id_tarefa = t.id_tarefa and tj.id_jbpm_task = jt.id_)";

}
