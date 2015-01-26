package br.com.infox.epp.processo.timer.query;

public interface TaskExpirationQuery {
    
    String PARAM_FLUXO = "fluxo";
    String PARAM_TASK_NAME = "tarefa";
    
    String GET_BY_FLUXO_AND_TASKNAME = "getByFluxoAndTaskName";
    String GET_BY_FLUXO_AND_TASKNAME_QUERY = "select o from TaskExpiration o where "
            + "o.fluxo = :" + PARAM_FLUXO + " and "
            + "o.tarefa = :" + PARAM_TASK_NAME;
}
