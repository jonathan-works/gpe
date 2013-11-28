package br.com.infox.epp.fluxo.xpdl;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.List;

import org.jbpm.graph.action.Script;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jdom2.Document;
import org.jdom2.Element;

import br.com.infox.epp.fluxo.xpdl.activities.ActivitiesXPDL;
import br.com.infox.epp.fluxo.xpdl.activities.ActivityXPDL;
import br.com.infox.epp.fluxo.xpdl.activities.AssignTaskXPDL;
import br.com.infox.epp.fluxo.xpdl.lane.LaneXPDL;
import br.com.infox.epp.fluxo.xpdl.lane.LanesXPDLFactory;
import br.com.infox.epp.fluxo.xpdl.transition.TransitionsXPDL;
import br.com.infox.ibpm.jpdl.JpdlXmlWriter;
import br.com.itx.util.XmlUtil;

public final class FluxoXPDL implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NO_NAME = "Indefinido ";

	private List<LaneXPDL> lanes;
	private ActivitiesXPDL activities;
	private TransitionsXPDL transitions;

	private FluxoXPDL(List<LaneXPDL> lanes, ActivitiesXPDL activities, TransitionsXPDL transitions) throws IllegalXPDLException {
		this.lanes = lanes;
		this.activities = activities;
		this.transitions = transitions;
	}

	/**
	 * Retorna o xml correspondente ao JPDL importado
	 * 
	 * @return
	 * @throws ParallelNodeXPDLException
	 */
	public String toJPDL(String cdFluxo) throws IllegalXPDLException {
		return JpdlXmlWriter.toString(toProcessDefinition(cdFluxo));
	}

	/**
	 * Retorna o ProcessDefinition correspondente ao JPDL importado return
	 * processDefinition
	 */
	public ProcessDefinition toProcessDefinition(String cdFluxo) throws IllegalXPDLException {
		ProcessDefinition definition = ProcessDefinition.createNewProcessDefinition();
		definition.setName(cdFluxo);
		definition.setDescription("Fluxo importado via arquivo xpdl.");

		for (LaneXPDL lane : lanes) {
			definition.getTaskMgmtDefinition().addSwimlane(lane.toSwimlane());
		}
		
		activities.changeParallelNodeInForkOrJoin(transitions.getTransitions());
		
		for (ActivityXPDL activity : activities.getActivities()) {
			Node node = activity.toNode();
			definition.addNode(node);
			if(activity instanceof AssignTaskXPDL) {
				AssignTaskXPDL assign = (AssignTaskXPDL) activity;
				assign.assignTask(definition);
			}
		}
		
		addEvents(definition);
		return definition;
	}
	
	public void assignActivitiesToLane(List<ActivityXPDL> activities) {
		for (LaneXPDL lane : lanes) {
			List<ActivityXPDL> list = lane.findActivitiesBelongingToLane(activities);
			for (ActivityXPDL activity : list) {
				activity.setLane(lane);
			}
		}
	}

	/**
	 * Cria uma instância do FluxoXPDL a partir de uma cadeia de bytes contendo
	 * o XPDL
	 * 
	 * @param bytes contendo o XPDL
	 * @return instância do FluxoXPDL
	 */
	public static FluxoXPDL createInstance(byte[] bytes) throws IllegalXPDLException {
		ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
		Document doc = XmlUtil.readDocument(stream);
		Element root = doc.getRootElement();
		return createInstance(root);
	}

	/**
	 * Cria uma instância do FluxoXPDL a partir do Element root do XPDL
	 * 
	 * @param bytes CONTENDO o XPDL
	 * @return instância do FluxoXPDL
	 */
	public static FluxoXPDL createInstance(Element root) throws IllegalXPDLException {
		List<LaneXPDL> lanes = LanesXPDLFactory.getLanes(root);
		
		ActivitiesXPDL activities = ActivitiesXPDL.createInstance(root);
		for (ActivityXPDL activity: activities.getActivities()) {
			for (LaneXPDL lane: lanes) {
				if (lane.contains(activity)) {
					activity.setLane(lane);
					break;
				}
			}
		}
		
		TransitionsXPDL transitions = TransitionsXPDL.createInstance(root, activities.getActivities());
		activities.adjustEndState();
		
		return new FluxoXPDL(lanes, activities, transitions);
	}

	/**
	 * Adiciona o tratamento de eventos
	 */
	private void addEvents(ProcessDefinition definition) {
		String[] supportedEventTypes = definition.getSupportedEventTypes();
		for (String e : supportedEventTypes) {
			addEvent(e, "br.com.infox.ibpm.util.JbpmEvents.raiseEvent(executionContext)", new Script(), definition);
		}
	}

	private void addEvent(String eventType, String expression, Action action, ProcessDefinition definition) {
		Event event = definition.getEvent(eventType);
		if (event == null) {
			event = new Event(eventType);
			definition.addEvent(event);
		}
		action.setAsync(false);
		if (action instanceof Script) {
			Script script = (Script) action;
			script.setExpression(expression);
		} else {
			action.setActionExpression(expression);
		}
		event.addAction(action);
	}

}
