package br.com.infox.ibpm.task.action;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.seam.exception.ApplicationException;
import br.com.infox.seam.path.PathResolver;

/**
 * Classe responsável por incluir a página referente a variavel taskPage
 * incluida na definição do fluxo.
 * 
 * @author Daniel
 * 
 */
@Name(value = TaskPageAction.NAME)
@Scope(ScopeType.PAGE)
public class TaskPageAction implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "taskPageAction";
    private String taskPagePath;
    public static final String TASK_PAGE_COMPONENT_PATH = "/WEB-INF/taskpages/";
    private static final String TASK_PAGE_SUFFIX = ".xhtml";
    private boolean hasTaskPage = false;
    
    @In
    private PathResolver pathResolver;

    /**
     * Verifica se a tarefa atual está utilizando uma variável taskPage. Se
     * estiver, obtem o caminho dessa página e atribuí a taskPagePath
     */
    private void readTaskPagePath() {
        List<VariableAccess> variableAccesses = getVariableAccesses();
        String taskPageName = null;
        for (VariableAccess va : variableAccesses) {
            String[] tokens = va.getMappedName().split(":");
            VariableType type = VariableType.valueOf(tokens[0]);
            if (type == VariableType.TASK_PAGE) {
                hasTaskPage = va.isWritable();
                taskPageName = tokens[1];
                String taskPagePath = TASK_PAGE_COMPONENT_PATH + taskPageName + TASK_PAGE_SUFFIX;
                String taskPageUrl = pathResolver.getRealPath(taskPagePath);
                if (taskPageUrl != null && new File(taskPageUrl).exists()) {
            		setTaskPagePath(taskPagePath);
                }
                break;
            }
        }
        if (taskPagePath == null && hasTaskPage) {
            throw new ApplicationException("Página de tarefa não encontrada: " + taskPageName);
        }
    }

    @SuppressWarnings(UNCHECKED)
    private List<VariableAccess> getVariableAccesses() {
        TaskInstance taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
        if (taskInstance != null) {
            TaskController taskController = taskInstance.getTask().getTaskController();
            if (taskController != null) {
                return taskController.getVariableAccesses();
            }
        }
        return Collections.emptyList();
    }

    public void setTaskPagePath(String taskPagePath) {
        this.taskPagePath = taskPagePath;
    }

    /**
     * Obtem o caminho da taskPage que deverá ser exibida nessa tarefa do fluxo
     * (taskInstance atual)
     * 
     * @return null se não foi definido um componente taskPage.
     */
    public String getTaskPagePath() {
        if (taskPagePath == null) {
            readTaskPagePath();
        }
        return taskPagePath;
    }

    public boolean getHasTaskPage() {
        if (!hasTaskPage) {
            readTaskPagePath();
        }
        return hasTaskPage;
    }
}
