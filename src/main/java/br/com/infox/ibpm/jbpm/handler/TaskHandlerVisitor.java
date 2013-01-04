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
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;


public class TaskHandlerVisitor {

	private boolean isMapped;
	private List<String> types;
	private List<String> variableList = new ArrayList<String>();
	private List<Task> visitedTasks = new ArrayList<Task>();

	public TaskHandlerVisitor (boolean isMapped) {
		this.isMapped = isMapped;
	}
	
	public TaskHandlerVisitor (boolean isMapped, List<String> types) {
		this.isMapped = isMapped;
		this.types = types;
	}
	
	public List<String> getVariables() {
		return variableList;
	}
	
	public void visit(Node n) {
		addVariables(n.getArrivingTransitions());
	}
	
	public void visit(Task t) {
		visitedTasks.add(t);
		Node n = (Node) t.getParent();
		visit((Node) t.getParent());
		Set<Transition> transitions = n.getArrivingTransitions();
		addVariables(transitions);
	}

	private void addVariables(Set<Transition> transitions) {
		if (transitions == null) {
			return;
		}
		for (Transition transition : transitions) {
			Node from = transition.getFrom();
			NodeType type = from.getNodeType();
			if (NodeType.Task.equals(type)) {
				TaskNode tn = (TaskNode) from;
				addTaskNodeVariables(tn);
			}
			switch (type) {
			case StartState:
				break;
			default:
				addVariables(from.getArrivingTransitions());
				break;
			}
		}
	}

	private void addTaskNodeVariables(TaskNode tn) {
		boolean filtered = types != null && types.size() > 0;
		for (Task tsk : tn.getTasks()) {
			TaskController tc = tsk.getTaskController();
			if (tc != null) {
				List<VariableAccess> accesses = tc.getVariableAccesses();
				for (VariableAccess v : accesses) {
					String mappedName = v.getMappedName();
					if (v.isWritable() && !mappedName.startsWith("page:")) {
						String name;
						if (isMapped) {
							name = mappedName;
						} else {
							name = v.getVariableName();
						}
						if (name != null && !"".equals(name)
								&& !variableList.contains(name)) {
							if (filtered) {
								if (types.contains(mappedName.split(":")[0])) {
									variableList.add(name);
								}
							} else {
								variableList.add(name);
							}
						}
					}
				}
			}
			if (!visitedTasks.contains(tsk)) {
				visit(tsk);
			}
		}
	}

}