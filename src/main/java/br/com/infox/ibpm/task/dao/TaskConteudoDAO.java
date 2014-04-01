package br.com.infox.ibpm.task.dao;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.ibpm.task.entity.TaskConteudo;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.ibpm.variable.VariableHandler;

@Name(TaskConteudoDAO.NAME)
@AutoCreate
public class TaskConteudoDAO extends DAO<TaskConteudo> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "taskConteudoDAO";
    
    
    @SuppressWarnings(UNCHECKED)
    private static String extractConteudo(Long taskId) {
        Session session = ManagedJbpmContext.instance().getSession();
        TaskInstance ti = (TaskInstance) session.get(TaskInstance.class, taskId);
        StringBuilder sb = new StringBuilder();
        TaskController taskController = ti.getTask().getTaskController();
        if (taskController != null) {
            List<VariableAccess> vaList = taskController.getVariableAccesses();
            for (VariableAccess v : vaList) {
                Object conteudo = ti.getVariable(v.getMappedName());
                if (v.isWritable() && conteudo != null) {
                    conteudo = JbpmUtil.instance().getConteudo(v, ti);
                    sb.append(VariableHandler.getLabel(v.getVariableName())).append(": ").append(conteudo).append("\n");
                }
            }
        }
        return getTextoIndexavel(sb.toString());
    }
    
    private static String getTextoIndexavel(String texto) {
        Document doc = Jsoup.parse(texto);
        return doc.body().text();
    }
    
    @Override
    public TaskConteudo persist(TaskConteudo taskConteudo) throws DAOException {
        taskConteudo.setConteudo(extractConteudo(taskConteudo.getIdTaskInstance()));
        return super.persist(taskConteudo);
    }
    
    @Override
    public TaskConteudo update(TaskConteudo taskConteudo) throws DAOException {
        taskConteudo.setConteudo(extractConteudo(taskConteudo.getIdTaskInstance()));
        return super.update(taskConteudo);
    }
    
    @Override
    protected FullTextEntityManager getEntityManager() {
        return (FullTextEntityManager) super.getEntityManager();
    }
}
