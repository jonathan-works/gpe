package br.com.infox.ibpm.jbpm.xpdl;

import java.io.Serializable;

import org.jbpm.graph.action.Script;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.ProcessDefinition;
import org.jdom.Element;

import br.com.infox.ibpm.jbpm.JpdlXmlWriter;
import br.com.infox.ibpm.jbpm.xpdl.activities.ActivitiesXPDL;
import br.com.infox.ibpm.jbpm.xpdl.activities.ActivityNotAllowedXPDLException;
import br.com.infox.ibpm.jbpm.xpdl.activities.IllegalActivityXPDLException;
import br.com.infox.ibpm.jbpm.xpdl.element.ParallelNodeXPDLException;
import br.com.infox.ibpm.jbpm.xpdl.lane.IllegalNumberPoolsXPDLException;
import br.com.infox.ibpm.jbpm.xpdl.lane.LanesXPDL;
import br.com.infox.ibpm.jbpm.xpdl.transition.IllegalTransitionXPDLException;
import br.com.infox.ibpm.jbpm.xpdl.transition.TransitionsXPDL;

public class FluxoXPDL implements Serializable {

	private static final long		serialVersionUID	= 1L;
	public static final String		NO_NAME				= "Indefinido ";
	
	private ActivitiesXPDL activities;
	private LanesXPDL lanes;
	private TransitionsXPDL transitions;
	private Element root;
	
	public FluxoXPDL(Element root) throws IllegalNumberPoolsXPDLException, ActivityNotAllowedXPDLException, IllegalActivityXPDLException, IllegalTransitionXPDLException {
		this.root = root;
		lanes = new LanesXPDL(root);
		activities = new ActivitiesXPDL(root);
		transitions = new TransitionsXPDL(root);
	}

	/**
	 * Retorna o xml correspondente ao JPDL importado
	 * @return
	 * @throws ParallelNodeXPDLException
	 */
	public String toJPDL() throws ParallelNodeXPDLException {
		ProcessDefinition definition = ProcessDefinition.createNewProcessDefinition();
		definition.setName(lanes.getPoolName());
		definition.setDescription("Fluxo importado via arquivo xpdl.");
		lanes.assignLanesToProcessDefinition(definition);
		transitions.createTransition(activities.getActivities());
		lanes.assignActivitiesToLane(activities.getActivities());
		activities.changeParallelNodeInForkOrJoin(transitions.getTransitions());
		transitions.assignTransitionToNode();
		activities.assignActivitiesToProcessDefinition(definition);
		activities.assignTaskToActivities(definition);
		addEvents(definition);
		return JpdlXmlWriter.toString(definition);
	}
	
	
	/**
	 * Retorna o element root do fluxo
	 * @return
	 */
	public Element getRoot() {
		return root;
	}
	
	/**
	 * Adiciona o tratamento de eventos
	 */
	private void addEvents(ProcessDefinition definition) {
		for (String e : ProcessDefinition.supportedEventTypes) {
			addEvent(e, "br.com.infox.ibpm.util.JbpmEvents.raiseEvent(executionContext)",
					new Script(), definition);
		}
	}

	private void addEvent(String eventType, String expression, Action action,
			ProcessDefinition definition) {
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
