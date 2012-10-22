package br.com.infox.ibpm.jbpm.xpdl.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jdom.Element;

import br.com.infox.ibpm.jbpm.xpdl.FluxoXPDL;
import br.com.infox.ibpm.jbpm.xpdl.element.ParallelNodeXPDLException;
import br.com.infox.ibpm.jbpm.xpdl.transition.TransitionXPDL;
import br.com.itx.util.XmlUtil;

public class ActivitiesXPDL {

	private List<ActivityXPDL>	activities;
	private int index;
	
	public ActivitiesXPDL(Element root) throws ActivityNotAllowedXPDLException, IllegalActivityXPDLException {
		index = 1;
		List<Element> atividades = checkWorkflowSection(root);
		activities = createAtivitiesList(XmlUtil.getChildren(atividades.get(0), "Activity"));
	}

	private List<Element> checkWorkflowSection(Element root) throws IllegalActivityXPDLException {
		List<Element> workFlowList = XmlUtil.getChildren(root, "WorkflowProcesses");
		if(workFlowList == null || workFlowList.isEmpty())
			throw new IllegalActivityXPDLException("Arquivo XPDL inválido. Não há a seção de definição dos Nós.");
		if(workFlowList.size() > 1)
			throw new IllegalActivityXPDLException("Arquivo XPDL inválido. Não há mais de uma seção de definição dos Nós.");
		Element atividades = XmlUtil.getChildByIndex(workFlowList.get(0), "WorkflowProcess", 1);
		List<Element> atividadesList = XmlUtil.getChildren(atividades, "Activities");
		return atividadesList;
	}
	
	private List<ActivityXPDL> createAtivitiesList(List<Element> list) throws ActivityNotAllowedXPDLException {
		if (list == null)
			return null;
		List<ActivityXPDL> activities = new ArrayList<ActivityXPDL>();
		for (Element ele : list) {
			activities.add(ActivityXPDLFactory.getAtividade(ele, FluxoXPDL.NO_NAME + index++));
		}
		return activities;
	}

	public List<ActivityXPDL> getActivities() {
		return activities;
	}
	
	public void assignActivitiesToProcessDefinition(ProcessDefinition definition) {
		if (activities == null)
			return;
		for (ActivityXPDL activity : activities) {
			Node node = activity.toNode();
			node.setProcessDefinition(definition);
			definition.addNode(node);
		}
	}
	
	public void changeParallelNodeInForkOrJoin(List<TransitionXPDL> transitions) throws ParallelNodeXPDLException {
		if (activities != null) {
			Map<ParallelActivityXPDL, Integer> parallels = getAllParallelXPDL(activities);
			for (ParallelActivityXPDL parallel : parallels.keySet()) {
				createForkJoinActivityXPDL(activities, parallel, parallels.get(parallel),
						transitions);
			}
		}
	}

	private List<TransitionXPDL> getAllTransitionsContainsActivity(
			List<TransitionXPDL> transitions, ParallelActivityXPDL parallel) {
		List<TransitionXPDL> lista = new ArrayList<TransitionXPDL>();
		for (TransitionXPDL transiction : transitions) {
			if (parallel.getId().equals(transiction.getFrom())
					|| parallel.getId().equals(transiction.getTo())) {
				lista.add(transiction);
			}
		}
		return lista;
	}

	private void createForkJoinActivityXPDL(List<ActivityXPDL> lista,
			ParallelActivityXPDL parallel, int position, List<TransitionXPDL> transitions)
			throws ParallelNodeXPDLException {
		List<TransitionXPDL> parallelTransitions = getAllTransitionsContainsActivity(transitions,
				parallel);
		List<ActivityXPDL> arrives = parallel.getArrives();
		List<ActivityXPDL> leaves = parallel.getLeaves();
		if (arrives != null && leaves != null && arrives.size() == 1 && leaves.size() > 1) {
			createForknode(lista, parallelTransitions, parallel, position);
		} else if (arrives != null && leaves != null && leaves.size() == 1 && arrives.size() > 1) {
			createJoinNode(lista, parallelTransitions, parallel, position);
		} else {
			throwParallelExceptionCheckImpossible(parallel);
		}
	}

	private void throwParallelExceptionCheckImpossible(ParallelActivityXPDL paralelo)
			throws ParallelNodeXPDLException {
		throw new ParallelNodeXPDLException("Impossível verificar se o Nó ("
				+ (paralelo.getName() != null ? paralelo.getName() : "Paralelo")
				+ ") é do tipo Join ou Fork.");
	}

	private void createJoinNode(List<ActivityXPDL> lista,
			List<TransitionXPDL> parallelTransitions, ParallelActivityXPDL parallel, int position) {
		JoinActivityXPDL node = new JoinActivityXPDL(parallel.getElement(), parallel.getName());
		Transition transition = null;
		for (TransitionXPDL trans : parallelTransitions) {
			if (trans.getFrom().equals(parallel.getId())) {
				transition = trans.toTransition(lista);
				transition.setFrom(node.toNode());
			} else if (trans.getTo().equals(parallel.getId())) {
				transition = trans.toTransition(lista);
				transition.setTo(node.toNode());
			}
		}
		node.setArrives(parallel.getArrives());
		node.setLeaves(parallel.getLeaves());
		lista.set(position, node);
	}

	private void createForknode(List<ActivityXPDL> lista,
			List<TransitionXPDL> parallelTransitions, ParallelActivityXPDL parallel, int position) {
		ForkActivityXPDL node = new ForkActivityXPDL(parallel.getElement(), parallel.getName());
		Transition transition = null;
		for (TransitionXPDL trans : parallelTransitions) {
			if (trans.getFrom().equals(parallel.getId())) {
				transition = trans.toTransition(lista);
				transition.setFrom(node.toNode());
			} else if (trans.getTo().equals(parallel.getId())) {
				transition = trans.toTransition(lista);
				transition.setTo(node.toNode());
			}
		}
		node.setArrives(parallel.getArrives());
		node.setLeaves(parallel.getLeaves());
		lista.set(position, node);
	}

	private Map<ParallelActivityXPDL, Integer> getAllParallelXPDL(List<ActivityXPDL> list) {
		Map<ParallelActivityXPDL, Integer> lista = null;
		lista = new HashMap<ParallelActivityXPDL, Integer>();
		int i = 0;
		for (ActivityXPDL n : list) {
			if (n instanceof ParallelActivityXPDL) {
				lista.put((ParallelActivityXPDL) n, i);
			}
			i++;
		}
		return lista;
	}
	
	public void assignTaskToActivities(ProcessDefinition definition) {
		if (activities == null)
			return;
		for (ActivityXPDL activity : activities) {
			if(activity instanceof AssignTaskXPDL) {
				AssignTaskXPDL assign = (AssignTaskXPDL)activity;
				assign.assignTask(definition);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder temp = new StringBuilder();
		if(activities == null) {
			temp.append("[Lanes] #lanes: 0");
		}
		else {
			temp.append("[Lanes] #lanes: " + activities.size());
			int i = 0;
			while(i < activities.size() && i < 8) {
				temp.append(activities.get(i++) + ", ");
			}
			if(i < activities.size())
				temp.append(" ... ");
		}
		return temp.toString();
	}
}
