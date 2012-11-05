package br.com.infox.ibpm.xpdl;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import org.jbpm.graph.action.Script;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.ProcessDefinition;
import org.jdom.Document;
import org.jdom.Element;

import br.com.infox.ibpm.jbpm.JpdlXmlWriter;
import br.com.infox.ibpm.xpdl.activities.ActivitiesXPDL;
import br.com.infox.ibpm.xpdl.element.ParallelNodeXPDLException;
import br.com.infox.ibpm.xpdl.lane.LanesXPDL;
import br.com.infox.ibpm.xpdl.transition.TransitionsXPDL;
import br.com.itx.util.XmlUtil;

public class FluxoXPDL implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NO_NAME = "Indefinido ";

	private LanesXPDL lanes;
	private ActivitiesXPDL activities;
	private TransitionsXPDL transitions;

	private FluxoXPDL(LanesXPDL lanes, ActivitiesXPDL activities, TransitionsXPDL transitions) throws IllegalXPDLException {
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

		lanes.assignLanesToProcessDefinition(definition);
		transitions.createTransition(activities.getActivities());
		lanes.assignActivitiesToLane(activities.getActivities());
		activities.changeParallelNodeInForkOrJoin(transitions.getTransitions());
		transitions.assignTransitionToNode();
		activities.assignActivitiesToProcessDefinition(definition);
		activities.assignTaskToActivities(definition);
		addEvents(definition);
		return definition;
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
	 * @param bytes contendo o XPDL
	 * @return instância do FluxoXPDL
	 */
	public static FluxoXPDL createInstance(Element root) throws IllegalXPDLException {
		LanesXPDL lanes = LanesXPDL.createInstance(root);
		ActivitiesXPDL activities = new ActivitiesXPDL(root);
		TransitionsXPDL transitions = new TransitionsXPDL(root);
		return new FluxoXPDL(lanes, activities, transitions);
	}

	/**
	 * Adiciona o tratamento de eventos
	 */
	private void addEvents(ProcessDefinition definition) {
		for (String e : ProcessDefinition.supportedEventTypes) {
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
