/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
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