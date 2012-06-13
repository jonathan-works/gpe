/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.jbpm;

import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.instantiation.Delegation;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;


public class TaskControllerHandler extends TaskController implements
		org.jbpm.taskmgmt.def.TaskControllerHandler
		{

	private static final long serialVersionUID = 1L;


	public void initializeTaskVariables(TaskInstance taskInstance,
			ContextInstance contextInstance, Token token) {
		TaskController taskController = taskInstance.getTask().getTaskController();
		Delegation delegation = taskController.getTaskControllerDelegation();
		taskController.setTaskControllerDelegation(null);
		taskController.initializeVariables(taskInstance);
		taskController.setTaskControllerDelegation(delegation);
	}


	public void submitTaskVariables(TaskInstance taskInstance,
			ContextInstance contextInstance, Token token) {
		super.submitParameters(taskInstance);
	}

}