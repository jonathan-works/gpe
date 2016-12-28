package br.com.infox.ibpm.util;

interface JbpmQueries {

    String PROCESS_NAMES_QUERY = "select pd.name from org.jbpm.graph.def.ProcessDefinition as pd "
            + "group by pd.name order by pd.name";

    String ALL_TASKS_QUERY = "select ti from org.jbpm.taskmgmt.exe.TaskInstance ti "
            + "where ti.isSuspended = false and ti.isOpen = true order by ti.name";

    String TOKENS_OF_AUTOMATIC_NODES_NOT_ENDED_QUERY = "select tk from org.jbpm.context.exe.variableinstance.LongInstance vi "
            + "inner join vi.token tk " + "where vi.name = 'processo' "
            + "and tk.end is null " + "and tk.lock is null "
            + "and tk.node.class IN ('N', 'M', 'D')"
            + "and tk.processInstance is not null "
            + "and exists (select 1 from Processo p where p.idProcesso = vi.value)";

    String NODE_BEANS_OF_AUTOMATIC_NODES_NOT_ENDED_QUERY = "select new br.com.infox.epp.processo.node.NodeBean("
            + "tk.id, tk.node.name, tk.node.class, p.numeroProcesso) "
            + "from org.jbpm.context.exe.variableinstance.LongInstance vi, Processo p "
            + "inner join vi.token tk "
            + "where vi.name = 'processo' "
            + "and tk.end is null " + "and tk.lock is null "
            + "and tk.node.class in ('N', 'M', 'D')"
            + "and tk.processInstance is not null "
            + "and p.idProcesso = vi.value";
}