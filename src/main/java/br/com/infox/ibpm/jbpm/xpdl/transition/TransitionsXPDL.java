package br.com.infox.ibpm.jbpm.xpdl.transition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jdom.Element;

import br.com.infox.ibpm.jbpm.xpdl.FluxoXPDL;
import br.com.infox.ibpm.jbpm.xpdl.activities.ActivityXPDL;
import br.com.itx.util.XmlUtil;

public class TransitionsXPDL implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<TransitionXPDL>	transitions;
	private int index;

	public TransitionsXPDL(Element root) throws IllegalTransitionXPDLException {
		index = 1;
		List<Element> workFlowList = XmlUtil.getChildren(root, "WorkflowProcesses");
		List<Element> workflows = XmlUtil.getChildren(workFlowList.get(0), "WorkflowProcess");
		List<Element> transitionsList = XmlUtil.getChildren(workflows.get(1), "Transitions");
		List<Element> transitionList = XmlUtil.getChildren(transitionsList.get(0), "Transition");
		transitions = createTransitionList(transitionList);
	}
	
	private List<TransitionXPDL> createTransitionList(List<Element> list)
			throws IllegalTransitionXPDLException {
		if (list == null)
			return null;
		List<TransitionXPDL> activities = new ArrayList<TransitionXPDL>();
		for (Element ele : list) {
			activities.add(new TransitionXPDL(ele, FluxoXPDL.NO_NAME + index++));
		}
		return activities;
	}
	
	public void createTransition(List<ActivityXPDL> atividades) {
		if (getTransitions() == null)
			return;
		for (TransitionXPDL tran : getTransitions()) {
			tran.toTransition(atividades);
		}
	}

	public void assignTransitionToNode() {
		if (getTransitions() == null)
			return;
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
