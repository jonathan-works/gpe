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
 * Classe respons�vel por incluir a p�gina referente a variavel
 * taskPage incluida na defini��o do fluxo.
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
	
	@In private FluxoManager fluxoManager;
	
	/**
	 * Verifica se a tarefa atual est� utilizando uma vari�vel taskPage.
	 * Se estiver, obtem o caminho dessa p�gina e atribu� a taskPagePath
	 */
	private void readTaskPagePath() {
		List<VariableAccess> variableAccesses = getVariableAccesses();
		for (VariableAccess va : variableAccesses) {
			String[] tokens = va.getMappedName().split(":");
			String type = tokens[0];
			if(type.equals(TASK_PAGE_COMPONENT_NAME)) {
				String pageName = tokens[1] + TASK_PAGE_SUFFIX;

				//Caso a pagina n�o seja encontrada no TASK_PAGE_COMPONENT_PATH � porque essa pagina � 
				//exclusiva do fluxo e vai estar no diret�rio que o nome � o c�digo
				URL taskPageUrl = getClass().getResource(TASK_PAGE_COMPONENT_PATH + pageName);
				if (taskPageUrl != null) {
					setTaskPagePath(taskPageUrl.toString());
				}
				break;
			}
		}
		if (taskPagePath == null) {
			throw new AplicationException("TaskPageAction n�o encontrada: " + taskPagePath);
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
	 * Obtem o caminho da taskPage que dever� ser exibida nessa
	 * tarefa do fluxo (taskInstance atual)
	 * @return null se n�o foi definido um componente taskPage.
	 */
	public String getTaskPagePath() {
		if(taskPagePath == null) {
			readTaskPagePath();
		}
		return taskPagePath;
	}
	
}