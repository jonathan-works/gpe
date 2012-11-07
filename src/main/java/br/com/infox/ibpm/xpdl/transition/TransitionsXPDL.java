package br.com.infox.ibpm.xpdl.transition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jdom.Element;

import br.com.infox.ibpm.xpdl.FluxoXPDL;
import br.com.infox.ibpm.xpdl.IllegalXPDLException;
import br.com.infox.ibpm.xpdl.activities.ActivityXPDL;
import br.com.itx.util.XmlUtil;

public class TransitionsXPDL implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<TransitionXPDL> transitions;

	public TransitionsXPDL(List<TransitionXPDL> transitions) {
		this.transitions = transitions;
	}
	
	private static List<TransitionXPDL> createTransitionList(List<Element> list) throws IllegalXPDLException {
		List<TransitionXPDL> activities = new ArrayList<TransitionXPDL>();
		int index = 0;
		for (Element ele : list) {
			activities.add(TransitionXPDL.createInstance(ele, FluxoXPDL.NO_NAME + index));
			index++;
		}
		return activities;
	}
	
	public static TransitionsXPDL createInstance(Element root) throws IllegalXPDLException {
		List<Element> workFlowList = XmlUtil.getChildren(root, "WorkflowProcesses");
		List<Element> workflows = XmlUtil.getChildren(workFlowList.get(0), "WorkflowProcess");
		List<Element> transitionsList = XmlUtil.getChildren(workflows.get(1), "Transitions");
		List<Element> transitionList = XmlUtil.getChildren(transitionsList.get(0), "Transition");
		List<TransitionXPDL> transitionXPDLList = createTransitionList(transitionList);
		
		return new TransitionsXPDL(transitionXPDLList);
	}
	
	public void createTransition(List<ActivityXPDL> atividades) {
		for (TransitionXPDL tran : getTransitions()) {
			tran.toTransition(atividades);
		}
	}

	public void assignTransitionToNode() {
		List<ActivityXPDL> empty = new ArrayList<ActivityXPDL>();
		for (TransitionXPDL activity : getTransitions()) {
			Transition transition = activity.toTransition(empty);
			Node from = transition.getFrom();
			Node to = transition.getTo();
			from.addLeavingTransition(transition);
			to.addArrivingTransition(transition);
		}
	}

	public List<TransitionXPDL> getTransitions() {
		return transitions;
	}

}
