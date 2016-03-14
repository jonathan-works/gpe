package br.com.infox.ibpm.util;

interface JbpmQueries {

    String PROCESS_NAMES_QUERY = "select pd.name from org.jbpm.graph.def.ProcessDefinition as pd "
            + "group by pd.name order by pd.name";

    String ALL_TASKS_QUERY = "select ti from org.jbpm.taskmgmt.exe.TaskInstance ti "
            + "where ti.isSuspended = false and ti.isOpen = true order by ti.name";

    String TOKENS_OF_AUTOMATIC_NODES_NOT_ENDED_QUERY = "SELECT t FROM org.jbpm.graph.exe.Token t "
            + "WHERE t.end IS NULL AND t.lock is null AND t.node.class IN ('N', 'M', 'D')";
}
