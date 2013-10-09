package br.com.infox.bpm.action;

import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.manager.FluxoManager;
import br.com.itx.exception.AplicationException;

/**
 * Classe responsável por incluir a página referente a variavel
 * taskPage incluida na definição do fluxo.
 * @author Daniel
 *
 */
@Name(value=TaskPageAction.NAME)
@Scope(ScopeType.PAGE)
public class TaskPageAction implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "taskPageAction";
	private String taskPagePath;
	public static final String TASK_PAGE_COMPONENT_NAME = "taskPage";
	public static final String TASK_PAGE_COMPONENT_PATH = "/taskpages/";
	private static final String TASK_PAGE_SUFFIX = ".xhtml";
	private boolean hasTaskPage = false;
	
	@In private FluxoManager fluxoManager;
	
	/**
	 * Verifica se a tarefa atual está utilizando uma variável taskPage.
	 * Se estiver, obtem o caminho dessa página e atribuí a taskPagePath
	 */
	private void readTaskPagePath() {
		List<VariableAccess> variableAccesses = getVariableAccesses();
		for (VariableAccess va : variableAccesses) {
			String[] tokens = va.getMappedName().split(":");
			String type = tokens[0];
			if(type.equals(TASK_PAGE_COMPONENT_NAME)) {
				hasTaskPage = true;
				String pageName = tokens[1] + TASK_PAGE_SUFFIX;

				//Caso a pagina não seja encontrada no TASK_PAGE_COMPONENT_PATH é porque essa pagina é 
				//exclusiva do fluxo e vai estar no diretório que o nome é o código
				URL taskPageUrl = getClass().getResource(TASK_PAGE_COMPONENT_PATH + pageName);
				if (taskPageUrl != null) {
					setTaskPagePath(taskPageUrl.toString());
				}
				break;
			}
		}
		if (taskPagePath == null && hasTaskPage) {
			throw new AplicationException("TaskPageAction não encontrada: " + taskPagePath);
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<VariableAccess> getVariableAccesses() {
		TaskInstance taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
		if (taskInstance != null) {
			TaskController taskController = taskInstance.getTask().getTaskController();
			if(taskController != null) {
				return taskController.getVariableAccesses();
			}
		}
		return Collections.emptyList();
	}

	public void setTaskPagePath(String taskPagePath) {
		this.taskPagePath = taskPagePath;
	}

	/**
	 * Obtem o caminho da taskPage que deverá ser exibida nessa
	 * tarefa do fluxo (taskInstance atual)
	 * @return null se não foi definido um componente taskPage.
	 */
	public String getTaskPagePath() {
		if(taskPagePath == null) {
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