package br.com.infox.bpm.action;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.ibpm.component.tree.TarefasTreeHandler;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.manager.SituacaoProcessoManager;
import br.com.itx.component.Util;
import br.com.itx.exception.AplicationException;
import br.com.itx.util.EntityUtil;

/**
 * Classe respons�vel pelas valida��es na transi��o do fluxo.
 * @author Daniel
 *
 */
public class TaskTransitionAction {
	
	@In private SituacaoProcessoManager situacaoProcessoManager;

	/**
	 * Verifica se a mesma tarefa n�o foi encerrada por outro usu�rio,
	 * afinal se a taskInstance for diferente da tempTask obtida no ato
	 * da requisi��o, significa que algu�m movimentou a tarefa enquanto
	 * o usu�rio estava com a janela aberta.
	 */
	public void canEndTask(TaskInstance currentTaskInstance) {
		TaskInstance tempTask = org.jboss.seam.bpm.TaskInstance.instance();
		if (currentTaskInstance != null) {
			if(tempTask == null || tempTask.getId() != currentTaskInstance.getId()) {
				FacesMessages.instance().clear();
				throw new AplicationException("Voc� n�o pode mais efetuar transa��es " +
								"neste registro, verifique se ele n�o foi movimentado");
			}
		}
	}

	/**
	 * Verifica se o usuario que est� transitando o processo no fluxo, pode
	 * visualizar a pr�xima <code>newTaskInstance</code> informada.
	 * @param nextTaskInstance - pr�xima tarefa para onde o fluxo levou o 
	 * processo.
	 * @return null se n�o puder.
	 */
	public TaskInstance canSeeNextTaskInstance(TaskInstance nextTaskInstance) {
		boolean canOpenTask = false;
		if (nextTaskInstance == null) {
			Util.setToEventContext("canClosePanel", true);
		} else {
			if (canOpenTask(nextTaskInstance.getId())) {
				canOpenTask = true;
			} else {
				Util.setToEventContext("canClosePanel", true);
			}
		}
		Util.setToEventContext("taskCompleted", true);
		return canOpenTask ? nextTaskInstance : null;
	}

	/**
	 * Verifica se a tarefa destino da transi��o apareceria no painel do 
	 * usuario o que indicia se o ele � da localizacao/papel da swimlane 
	 * da tarefa criada 
	 * @param currentTaskId
	 * @return true se ele pode visualizar a pr�xima tarefa
	 */
	@SuppressWarnings("rawtypes")
	private boolean canOpenTask(long currentTaskId) {
		JbpmUtil.getJbpmSession().flush();
		Events.instance().raiseEvent(TarefasTreeHandler.FILTER_TAREFAS_TREE);
//		List resultList = EntityUtil.getEntityManager().createQuery(
//				"select o.idTaskInstance from SituacaoProcesso o " +
//				"where o.idTaskInstance = :ti")
//			.setParameter("ti", currentTaskId)
//			.getResultList();
		return situacaoProcessoManager.existemTarefasEmAberto(currentTaskId);
	}
	
}