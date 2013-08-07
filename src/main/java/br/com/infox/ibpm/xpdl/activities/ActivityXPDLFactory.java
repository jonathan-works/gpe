package br.com.infox.ibpm.xpdl.activities;

import java.util.List;

import org.jdom.Element;

import br.com.itx.util.XmlUtil;

public final class ActivityXPDLFactory {
	
	private static final String START_EVENT = "StartEvent";
    private static final String END_EVENT = "EndEvent";
    private static final String SUB_FLOW = "SubFlow";
    private static final String TASK = "Task";
    private static final String IMPLEMENTATION = "Implementation";
    private static final String ROUTE = "Route";
    private static final String GATEWAY_TYPE = "GatewayType";
    private static final String TRIGGER = "Trigger";
    private static final String INTERMEDIATE_EVENT = "IntermediateEvent";
    private static final String EVENT = "Event";
    private static final String	PARALLEL = "Parallel";
    
    private ActivityXPDLFactory(){
        super();
    }
	
	public static ActivityXPDL createInstance(Element element, String name) throws ActivityNotAllowedXPDLException {
		ActivityXPDL activity = null;
		if (isStartState(element)) {
			activity = new StartActivityXPDL(element, name);
		} else if (isEndState(element)) {
			activity = new EndActivityXPDL(element, name);
		} else if (isProcessState(element)) {
			activity = new SubProcessActivityXPDL(element, name);
		} else if (isTaskNode(element)) {
			activity = new TaskActivityXPDL(element, name);
		} else if (isMailNode(element)) {
			activity = new MailActivityXPDL(element, name);
		} else if (isDecisionNode(element)) {
			activity = new DecisionActivityXPDL(element, name);
		} else if (isParallelNode(element)) {
			activity = new ParallelActivityXPDL(element, name);
		} else if (isSystemNode(element)) {
			activity = new SystemActivityXPDL(element, name);
		} else {
			throw new ActivityNotAllowedXPDLException("Tipo de nó não permitido no sistema. Nome do nó: '" + XmlUtil.getAttributeValue(element, "Name") + "'.");
		}
		return activity;
	}
	
	private static boolean isSystemNode(Element element) {
		List<Element> eventList = XmlUtil.getChildren(element, EVENT);
		if(!eventList.isEmpty()) {
			List<Element> intermediate = XmlUtil.getChildren(eventList.get(0), INTERMEDIATE_EVENT);
			if (!intermediate.isEmpty()) {
				String value = XmlUtil.getAttributeValue(intermediate.get(0), TRIGGER);
				return "None".equals(value);
			}
		}
		return false;
	}

	private static boolean isParallelNode(Element element) {
		List<Element> routeList = XmlUtil.getChildren(element, ROUTE);
		if(!routeList.isEmpty()) {
			String gateway = XmlUtil.getAttributeValue(routeList.get(0), GATEWAY_TYPE);
			return PARALLEL.equalsIgnoreCase(gateway);
		}
		return false;
	}

	private static boolean isDecisionNode(Element element) {
		List<Element> routeList = XmlUtil.getChildren(element, ROUTE);
		if(!routeList.isEmpty()) {
			String gateway = XmlUtil.getAttributeValue(routeList.get(0), GATEWAY_TYPE);
			return gateway == null || gateway.isEmpty();
		}
		return false;
	}

	private static boolean isMailNode(Element element) {
		List<Element> eventList = XmlUtil.getChildren(element, EVENT);
		if(!eventList.isEmpty()) {
			List<Element> intermediate = XmlUtil.getChildren(eventList.get(0), INTERMEDIATE_EVENT);
			if(!intermediate.isEmpty()) {
				String value = XmlUtil.getAttributeValue(intermediate.get(0), TRIGGER);
				return "Message".equalsIgnoreCase(value);
			}
		}
		return false;
	}

	private static boolean isTaskNode(Element element) {
		List<Element> implList = XmlUtil.getChildren(element, IMPLEMENTATION);
		if(!implList.isEmpty()) {
			List<Element> taskList = XmlUtil.getChildren(implList.get(0), TASK);
			return !taskList.isEmpty();
		}
		return false;
	}
	
	/**
	 * Método responsável por identificar se o element é um subprocesso (ProcessState)
	 * @param element
	 * @return
	 */
	private static boolean isProcessState(Element element) {
		List<Element> implList = XmlUtil.getChildren(element, IMPLEMENTATION);
		if(!implList.isEmpty()) {
			List<Element> subFlow = XmlUtil.getChildren(implList.get(0), SUB_FLOW);
			return !subFlow.isEmpty();
		}
		return false;
	}

	private static boolean isEndState(Element element) {
		List<Element> eventList = XmlUtil.getChildren(element, EVENT);
		if(!eventList.isEmpty()) {
			List<Element> endEvent = XmlUtil.getChildren(eventList.get(0), END_EVENT);
			return !endEvent.isEmpty();
		}
		return false;
	}

	private static boolean isStartState(Element element) {
		List<Element> eventList = XmlUtil.getChildren(element, EVENT);
		if(!eventList.isEmpty()) {
			List<Element> startEventList = XmlUtil.getChildren(eventList.get(0), START_EVENT);
			return !startEventList.isEmpty();
		}
		return false;
	}

}
