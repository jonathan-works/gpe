package br.com.infox.bpm.action;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.ibpm.entity.Fluxo;
import br.com.itx.util.EntityUtil;

import br.com.itx.component.Util;
import br.com.itx.exception.AplicationException;

/**
 * Classe respons�vel por incluir a p�gina referente a variavel
 * taskPage incluida na defini��o do fluxo.
 * @author Daniel
 *
 */
@Name(value=TaskPageAction.NAME)
@Scope(ScopeType.PAGE)
@BypassInterceptors
public class TaskPageAction implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "taskPageAction";
	private String taskPagePath;
	public static final String TASK_PAGE_COMPONENT_NAME = "taskPage";
	private static final String TASK_PAGE_COMPONENT_PATH = "/WEB-INF/xhtml/taskPages/";
	private static final String TASK_PAGE_SUFFIX = ".xhtml";
	
	/**
	 * Verifica se a tarefa atual est� utilizando uma vari�vel taskPage.
	 * Se estiver, obtem o caminho dessa p�gina e atribu� a taskPagePath
	 */
	private void readTaskPagePath() {
		TaskInstance taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
		List<VariableAccess> variableAccesses = getVariableAccesses();
		Util util = new Util();
		for (VariableAccess va : variableAccesses) {
			String[] tokens = va.getMappedName().split(":");
			String type = tokens[0];
			if(type.equals(TASK_PAGE_COMPONENT_NAME)) {
				StringBuilder sb = new StringBuilder();
				sb.append(TASK_PAGE_COMPONENT_PATH);
				String pdName = taskInstance.getProcessInstance().getProcessDefinition().getName();
				String pageName = tokens[1] + TASK_PAGE_SUFFIX;

				//Caso a pagina n�o seja encontrada no TASK_PAGE_COMPONENT_PATH � porque essa pagina � 
				//exclusiva do fluxo e vai estar no diret�rio que o nome � o c�digo
				if (util.fileExists(sb.toString() + pageName)) {
					setTaskPagePath(sb.toString() + pageName);
				} else {
					sb.append(getCodFluxoByDescricao(pdName))
					.append("/")
					.append(pageName);
					setTaskPagePath(sb.toString());
				}
				break;
			}
		}
		if (taskPagePath != null && !util.fileExists(taskPagePath)) {
			AplicationException.createMessage(
					"Obter o caminho da TaskPageAction", "readTaskPagePath()", AplicationException.class.getName(), "BPM");
			throw new AplicationException("TaskPageAction n�o encontrada: " + taskPagePath);
		}
	}
	
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

	/**
	 * Obtem o c�digo do Fluxo baseado na descricao do mesmo, sendo
	 * que a descricao � uma chave �nica na entitidade Fluxo.java
	 * @param descricao
	 * @return
	 */
	private String getCodFluxoByDescricao(String descricao) {
		String fluxoByDescricao = "select o from Fluxo o where " +
								  "o.fluxo like :descricao";
		Query query = EntityUtil.getEntityManager().createQuery(fluxoByDescricao);
		query.setParameter("descricao", descricao);
		Fluxo f = EntityUtil.getSingleResult(query);
		if(f != null) {
			return f.getCodFluxo();
		}
		return null;
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