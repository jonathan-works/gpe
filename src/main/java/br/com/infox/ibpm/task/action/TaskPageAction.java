package br.com.infox.ibpm.task.action;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.variable.components.TaskpageDefinition;
import br.com.infox.ibpm.variable.components.VariableDefinitionService;
import br.com.infox.seam.exception.ApplicationException;

/**
 * Classe responsável por incluir a página referente a variavel taskPage
 * incluida na definição do fluxo.
 * 
 * @author Daniel
 * 
 */
@Named
@ViewScoped
public class TaskPageAction implements Serializable {

    private static final long serialVersionUID = 1L;
    private String taskPagePath;
    private boolean hasTaskPage = false;

    @Inject
    private VariableDefinitionService variableDefinitionService;

    /**
     * Verifica se a tarefa atual está utilizando uma variável taskPage. Se
     * estiver, obtem o caminho dessa página e atribuí a taskPagePath
     */
    private void readTaskPagePath(TaskInstance taskInstance) {
        List<VariableAccess> variableAccesses = getVariableAccesses(taskInstance);
        String taskPageName = null;
        for (VariableAccess va : variableAccesses) {
            String[] tokens = va.getMappedName().split(":");
            VariableType type = VariableType.valueOf(tokens[0]);
            if (type == VariableType.TASK_PAGE) {
                hasTaskPage = va.isWritable();
                taskPageName = tokens[1];
                TaskpageDefinition taskPage = variableDefinitionService.getTaskPage(taskPageName);
                if(hasTaskPage && taskPage == null) {
                	throw new ApplicationException("Página de tarefa não encontrada: " + taskPageName);
                }
                String taskPagePath = taskPage.getXhtmlPath();
        		setTaskPagePath(taskPagePath);
                break;
            }
        }
        if (taskPagePath == null && hasTaskPage) {
            throw new ApplicationException("Página de tarefa não encontrada: " + taskPageName);
        }
    }

    private List<VariableAccess> getVariableAccesses(TaskInstance taskInstance) {
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
    public String getTaskPagePath(TaskInstance taskInstance) {
        if (taskPagePath == null) {
            readTaskPagePath(taskInstance);
        }
        return taskPagePath;
    }

    public boolean getHasTaskPage(TaskInstance taskInstance) {
        if (!hasTaskPage) {
            readTaskPagePath(taskInstance);
        }
        return hasTaskPage;
    }
}
