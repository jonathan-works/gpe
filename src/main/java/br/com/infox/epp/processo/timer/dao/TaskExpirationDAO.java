package br.com.infox.epp.processo.timer.dao;

import static br.com.infox.epp.processo.timer.query.TaskExpirationQuery.GET_BY_FLUXO_AND_TASKNAME;
import static br.com.infox.epp.processo.timer.query.TaskExpirationQuery.PARAM_FLUXO;
import static br.com.infox.epp.processo.timer.query.TaskExpirationQuery.PARAM_TASK_NAME;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.processo.timer.TaskExpiration;

@Name(TaskExpirationDAO.NAME)
@AutoCreate
public class TaskExpirationDAO extends DAO<TaskExpiration> {
    static final String NAME = "taskExpirationDAO";
    private static final long serialVersionUID = 1L;
    
    public TaskExpiration getByFluxoAndTaskName(Fluxo fluxo, String taskName) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_FLUXO, fluxo);
        params.put(PARAM_TASK_NAME, taskName);
        return getNamedSingleResult(GET_BY_FLUXO_AND_TASKNAME, params);
    }
}