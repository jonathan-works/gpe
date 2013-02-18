package br.com.infox.ibpm.xpdl.activities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.EndState;
import org.jdom.Element;

import br.com.infox.ibpm.xpdl.FluxoXPDL;
import br.com.infox.ibpm.xpdl.IllegalXPDLException;
import br.com.infox.ibpm.xpdl.element.ParallelNodeXPDLException;
import br.com.infox.ibpm.xpdl.transition.TransitionXPDL;
import br.com.itx.util.XmlUtil;

public class ActivitiesXPDL implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<ActivityXPDL>	activities;
	 
	public ActivitiesXPDL(List<ActivityXPDL> activities) {
		this.activities = activities;
	}

	private static List<ActivityXPDL> createAtivitiesList(List<Element> list) throws ActivityNotAllowedXPDLException {
		List<ActivityXPDL> activityXPDLList = new ArrayList<ActivityXPDL>();
		int index = 0;
		for (Element ele : list) {
			activityXPDLList.add(ActivityXPDLFactory.createInstance(ele, FluxoXPDL.NO_NAME + index));
			index++;
		}
		return activityXPDLList;
	}
	
	
	public static ActivitiesXPDL createInstance(Element root) throws IllegalXPDLException {
		List<Element> workFlowList = XmlUtil.getChildren(root, "WorkflowProcesses");
		if(workFlowList == null || workFlowList.isEmpty()) {
			throw new IllegalActivityXPDLException("Arquivo XPDL inválido. Não há a seção de definição dos Nós.");
		}
		if(workFlowList.size() > 1) {
			throw new IllegalActivityXPDLException("Arquivo XPDL inválido. Não há mais de uma seção de definição dos Nós.");
		}
		Element activitiesElement = XmlUtil.getChildByIndex(workFlowList.get(0), "WorkflowProcess", 1);
		List<Element> activitiesList = XmlUtil.getChildren(activitiesElement, "Activities");
		
		List<ActivityXPDL> activityXPDLList = createAtivitiesList(XmlUtil.getChildren(activitiesList.get(0), "Activity"));
		
		return new ActivitiesXPDL(activityXPDLList);
	}

	public List<ActivityXPDL> getActivities() {
		return activities;
	}
	
	public void changeParallelNodeInForkOrJoin(List<TransitionXPDL> transitions) throws ParallelNodeXPDLException {
		Map<ParallelActivityXPDL, Integer> parallels = getAllParallelXPDL(activities);
		for (Entry<ParallelActivityXPDL, Integer> entry : parallels.entrySet()) {
			ParallelActivityXPDL parallel = entry.getKey();
			createForkJoinActivityXPDL(activities, parallel, entry.getValue(), transitions);
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
			throw new ParallelNodeXPDLException("Impossível verificar se o Nó ("
					+ (parallel.getName() != null ? parallel.getName() : "Paralelo")
					+ ") é do tipo Join ou Fork.");
		}
	}

	private void createJoinNode(List<ActivityXPDL> lista,
			List<TransitionXPDL> parallelTransitions, ParallelActivityXPDL parallel, int position) {
		JoinActivityXPDL node = new JoinActivityXPDL(parallel.getElement(), parallel.getName());
		Transition transition = null;
		for (TransitionXPDL trans : parallelTransitions) {
			if (trans.getFrom().equals(parallel.getId())) {
				transition = trans.toTransition();
				transition.setFrom(node.toNode());
			} else if (trans.getTo().equals(parallel.getId())) {
				transition = trans.toTransition();
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
				transition = trans.toTransition();
				transition.setFrom(node.toNode());
			} else if (trans.getTo().equals(parallel.getId())) {
				transition = trans.toTransition();
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

	public void adjustEndState() {
		Node endState = null;
		Iterator<ActivityXPDL> iter = activities.iterator();
		while (iter.hasNext()) {
			Node node = iter.next().toNode();
			if (node instanceof EndState) {
				if (endState == null) {
					endState = node;
					endState.setName("Término");
				} else {
					iter.remove();
					
					for (Object o : node.getArrivingTransitions()) {
						((Transition) o).setTo(endState);
					}
				}
			}
		}
	}
	
}
