package br.com.infox.ibpm.xpdl.transition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	
	private static List<TransitionXPDL> createTransitionList(List<Element> list, List<ActivityXPDL> activities) throws IllegalXPDLException {
		List<TransitionXPDL> transitionList = new ArrayList<TransitionXPDL>();
		int index = 0;
		for (Element ele : list) {
			TransitionXPDL transition = TransitionXPDL.createInstance(ele, FluxoXPDL.NO_NAME + index);
			transition.setActivities(activities);
			transitionList.add(transition);
			index++;
		}
		return transitionList;
	}
	
	public static TransitionsXPDL createInstance(Element root, List<ActivityXPDL> activities) throws IllegalXPDLException {
		List<Element> workFlowList = XmlUtil.getChildren(root, "WorkflowProcesses");
		List<Element> workflows = XmlUtil.getChildren(workFlowList.get(0), "WorkflowProcess");
		List<Element> transitionsList = XmlUtil.getChildren(workflows.get(1), "Transitions");
		List<Element> transitionList = XmlUtil.getChildren(transitionsList.get(0), "Transition");
		List<TransitionXPDL> transitionXPDLList = createTransitionList(transitionList, activities);
		
		return new TransitionsXPDL(transitionXPDLList);
	}
	
	public List<TransitionXPDL> getTransitions() {
		return transitions;
	}

}
