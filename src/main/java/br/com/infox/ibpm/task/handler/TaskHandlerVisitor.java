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
package br.com.infox.ibpm.task.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;

import br.com.infox.core.constants.WarningConstants;


public class TaskHandlerVisitor {

	private boolean isMapped;
	private List<String> types;
	private List<String> variableList = new ArrayList<String>();
	private List<Task> visitedTasks = new ArrayList<Task>();
	private List<Transition> visitedTransitions = new ArrayList<Transition>();

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
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public void visit(Node n) {
		addVariables(n.getArrivingTransitions());
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public void visit(Task t) {
		visitedTasks.add(t);
		Node n = (Node) t.getParent();
		visit((Node) t.getParent());
		Set<Transition> transitions = n.getArrivingTransitions();
		addVariables(transitions);
	}

	@SuppressWarnings(WarningConstants.UNCHECKED)
	private void addVariables(Set<Transition> transitions) {
		if (transitions == null) {
			return;
		}
		for (Transition transition : transitions) {
			if (visitedTransitions.contains(transition))	{
				continue;
			} else {
				visitedTransitions.add(transition);
			}
			Node from = transition.getFrom();
			NodeType type = from.getNodeType();
			if (NodeType.Task.equals(type)) {
				TaskNode tn = (TaskNode) from;
				addTaskNodeVariables(tn);
			}
			//TODO: Esse equals funciona?
			if (!type.equals(NodeType.StartState)) {
				addVariables(from.getArrivingTransitions());
			}
		}
	}

	@SuppressWarnings(WarningConstants.UNCHECKED)
	private void addTaskNodeVariables(TaskNode tn) {
		boolean filtered = types != null && types.size() > 0;
		for (Object o : tn.getTasks()) {
			Task tsk = (Task) o;
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