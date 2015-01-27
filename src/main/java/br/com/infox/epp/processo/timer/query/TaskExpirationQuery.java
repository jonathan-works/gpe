package br.com.infox.epp.processo.timer.query;

public interface TaskExpirationQuery {
    
    String PARAM_FLUXO = "fluxo";
    String PARAM_TASK_NAME = "tarefa";
    String PARAM_TASK_NAMES = "taskNames";
    
    String GET_BY_FLUXO_AND_TASKNAME = "getByFluxoAndTaskName";
    String GET_BY_FLUXO_AND_TASKNAME_QUERY = "select o from TaskExpiration o where "
            + "o.fluxo = :" + PARAM_FLUXO + " and "
            + "o.tarefa = :" + PARAM_TASK_NAME;
    
    String CLEAR_UNUSED_TASK_EXPIRATIONS = "clearUnusedTaskExpirations";
    String CLEAR_UNUSED_TASK_EXPIRATIONS_QUERY = "delete from TaskExpiration te "
            + "where te.fluxo = :" + PARAM_FLUXO + " "
            + "and te.tarefa not in (:" + PARAM_TASK_NAMES + ")";
    
    String DELETE_BY_FLUXO = "deleteByFluxo";
    String DELETE_BY_FLUXO_QUERY = "delete from TaskExpiration te "
            + "where te.fluxo = :" + PARAM_FLUXO;
}
