package br.com.infox.epp.processo.timer.action;

import java.io.Serializable;
import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.timer.TaskExpiration;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.ibpm.process.definition.fitter.TaskFitter;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(TaskExpirationAction.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class TaskExpirationAction implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "taskExpirationAction";
    private static final LogProvider LOG = Logging.getLogProvider(TaskExpirationAction.class);
    
    @In
    private GenericDAO genericDAO;
    @In
    private TaskFitter taskFitter;
    
    private Tarefa tarefa;
    private Date expiration;
    private String transition;

    public void addExpiration() {
        TaskExpiration taskExpiration = new TaskExpiration();
        taskExpiration.setTarefa(taskFitter.getTarefaAtual());
        taskExpiration.setExpiration(expiration);
        taskExpiration.setTransition(transition);
        try {
            genericDAO.persist(taskExpiration);
        } catch (DAOException e) {
            LOG.error("Falha ao gravar data de expiração da tarefa", e); // TODO internacionalizar?
        }
    }
    
    public void removeExpiration(TaskExpiration taskExpiration) {
        try {
            genericDAO.remove(taskExpiration);
        } catch (DAOException e) {
            LOG.error("Falha ao remover data de expiração da tarefa", e); // TODO internacionalizar?
        }
    }
    
    public Tarefa getTarefa() {
        return tarefa;
    }

    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public String getTransition() {
        return transition;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }

}
