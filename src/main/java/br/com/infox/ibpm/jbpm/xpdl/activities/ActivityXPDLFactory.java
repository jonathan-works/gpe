package br.com.infox.ibpm.jbpm.xpdl.activities;

import java.util.List;

import org.jdom.Element;

import br.com.itx.util.XmlUtil;

public class ActivityXPDLFactory {
	
	private static final String	PARALLEL = "Parallel";
	
	public static ActivityXPDL getAtividade(Element element, String name) throws ActivityNotAllowedXPDLException {
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
			throw new ActivityNotAllowedXPDLException("tipo de nó não permitido no e-PA. Nome do nó: '" + XmlUtil.getAttributeValue(element, "Name") + "'.");
		}
		return activity;
	}
	
	private static boolean isSystemNode(Element element) {
		List<Element> eventList = XmlUtil.getChildren(element, "Event");
		if(eventList != null && !eventList.isEmpty()) {
			List<Element> intermediate = XmlUtil.getChildren(eventList.get(0), "IntermediateEvent");
			if(intermediate != null && !intermediate.isEmpty()) {
				String value = XmlUtil.getAttributeValue(intermediate.get(0), "Trigger");
				return "None".equals(value);
			}
		}
		return false;
	}

	private static boolean isParallelNode(Element element) {
		List<Element> routeList = XmlUtil.getChildren(element, "Route");
		if(routeList != null && !routeList.isEmpty()) {
			String gateway =XmlUtil.getAttributeValue(routeList.get(0), "GatewayType");
			return PARALLEL.equalsIgnoreCase(gateway);
		}
		return false;
	}

	private static boolean isDecisionNode(Element element) {
		List<Element> routeList = XmlUtil.getChildren(element, "Route");
		if(routeList != null && !routeList.isEmpty()) {
			String gateway = XmlUtil.getAttributeValue(routeList.get(0), "GatewayType");
			return gateway == null || gateway.isEmpty();
		}
		return false;
	}

	private static boolean isMailNode(Element element) {
		List<Element> eventList = XmlUtil.getChildren(element, "Event");
		if(eventList != null && !eventList.isEmpty()) {
			List<Element> intermediate = XmlUtil.getChildren(eventList.get(0), "IntermediateEvent");
			if(intermediate != null && !intermediate.isEmpty()) {
				String value = XmlUtil.getAttributeValue(intermediate.get(0), "Trigger");
				return value != null && "Message".equalsIgnoreCase(value);
			}
		}
		return false;
	}

	private static boolean isTaskNode(Element element) {
		List<Element> implList = XmlUtil.getChildren(element, "Implementation");
		if(implList != null && !implList.isEmpty()) {
			List<Element> taskList = XmlUtil.getChildren(implList.get(0), "Task");
			return taskList != null && !taskList.isEmpty();
		}
		return false;
	}
	
	/**
	 * Método responsável por identificar se o element é um subprocesso (ProcessState)
	 * @param element
	 * @return
	 */
	private static boolean isProcessState(Element element) {
		List<Element> implList = XmlUtil.getChildren(element, "Implementation");
		if(implList != null && !implList.isEmpty()) {
			List<Element> subFlow = XmlUtil.getChildren(implList.get(0), "SubFlow");
			return subFlow != null && !subFlow.isEmpty();
		}
		return false;
	}

	private static boolean isEndState(Element element) {
		List<Element> eventList = XmlUtil.getChildren(element, "Event");
		if(eventList != null && !eventList.isEmpty()) {
			List<Element> endEvent = XmlUtil.getChildren(eventList.get(0), "EndEvent");
			return endEvent != null && !endEvent.isEmpty();
		}
		return false;
	}

	private static boolean isStartState(Element element) {
		List<Element> eventList = XmlUtil.getChildren(element, "Event");
		if(eventList != null && !eventList.isEmpty()) {
			List<Element> startEventList = XmlUtil.getChildren(eventList.get(0), "StartEvent");
			return startEventList != null && !startEventList.isEmpty();
		}
		return false;
	}

}
