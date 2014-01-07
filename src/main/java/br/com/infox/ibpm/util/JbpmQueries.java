package br.com.infox.ibpm.util;

interface JbpmQueries {

    String PROCESS_NAMES_QUERY = "select pd.name from org.jbpm.graph.def.ProcessDefinition as pd "
            + "group by pd.name order by pd.name";

}
