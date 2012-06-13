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
package br.com.infox.ibpm.jbpm.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.Fork;
import org.jbpm.graph.node.Join;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;


public class TaskHandlerVisitor {

	private boolean isMapped;
	private List<String> variableList = new ArrayList<String>();
	private List<Task> visitedTasks = new ArrayList<Task>();

	public TaskHandlerVisitor (boolean isMapped) {
		this.isMapped = isMapped;
	}
	
	public List<String> getVariables() {
		return variableList;
	}
	
	public void visit(Task t) {
		visitedTasks.add(t);
		Node n = (Node) t.getParent();
		Set<Transition> transitions = n.getArrivingTransitions();
		if (transitions == null) {
			return;
		}
		addVariables(transitions);
	}

	private void addVariables(Set<Transition> transitions) {
		if (transitions == null) {
			return;
		}
		for (Transition transition : transitions) {
			Node from = transition.getFrom();
			if (from instanceof TaskNode) {
				TaskNode tn = (TaskNode) from;
				addTaskNodeVariables(tn);
			} else if ((from instanceof Fork)
					|| (from instanceof Join)
					|| (from instanceof ProcessState)) {
				addVariables(from.getArrivingTransitions());
			}
		}
	}

	private void addTaskNodeVariables(TaskNode tn) {
		for (Task tsk : tn.getTasks()) {
			TaskController tc = tsk.getTaskController();
			if (tc != null) {
				List<VariableAccess> accesses = tc.getVariableAccesses();
				for (VariableAccess v : accesses) {
					if (v.isWritable() && !v.getMappedName().startsWith("page:")) {
						String name;
						if (isMapped) {
							name = v.getMappedName();
						} else {
							name = v.getVariableName();
						}
						if (name != null && ! "".equals(name) && !variableList.contains(name)) {
							variableList.add(name);
						}
					}
				}
			}
			if (! visitedTasks.contains(tsk)) {
				visit(tsk);
			}
		}
	}


}