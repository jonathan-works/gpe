package br.com.infox.epp.processo.timer.dao;

import static br.com.infox.epp.processo.timer.query.TaskExpirationQuery.CLEAR_UNUSED_TASK_EXPIRATIONS;
import static br.com.infox.epp.processo.timer.query.TaskExpirationQuery.DELETE_BY_FLUXO;
import static br.com.infox.epp.processo.timer.query.TaskExpirationQuery.GET_BY_FLUXO_AND_TASKNAME;
import static br.com.infox.epp.processo.timer.query.TaskExpirationQuery.PARAM_FLUXO;
import static br.com.infox.epp.processo.timer.query.TaskExpirationQuery.PARAM_TASK_NAME;
import static br.com.infox.epp.processo.timer.query.TaskExpirationQuery.PARAM_TASK_NAMES;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.processo.timer.TaskExpiration;

@Stateless
@AutoCreate
@Name(TaskExpirationDAO.NAME)
public class TaskExpirationDAO extends DAO<TaskExpiration> {
    
    static final String NAME = "taskExpirationDAO";
    private static final long serialVersionUID = 1L;
    
    public TaskExpiration getByFluxoAndTaskName(Fluxo fluxo, String taskName) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_FLUXO, fluxo);
        params.put(PARAM_TASK_NAME, taskName);
        return getNamedSingleResult(GET_BY_FLUXO_AND_TASKNAME, params);
    }

    public void clearUnusedTaskExpirations(Fluxo fluxo, Set<String> taskNames) throws DAOException {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_FLUXO, fluxo);
        if (taskNames != null && !taskNames.isEmpty()) {
            params.put(PARAM_TASK_NAMES, taskNames);
            executeNamedQueryUpdate(CLEAR_UNUSED_TASK_EXPIRATIONS, params);
        } else {
            executeNamedQueryUpdate(DELETE_BY_FLUXO, params);
        }
    }
}