package br.com.infox.ibpm.jbpm.fitter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jbpm.graph.action.Script;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.ProcessDefinition;

import br.com.infox.ibpm.jbpm.handler.EventHandler;
import br.com.itx.util.ReflectionsUtil;

@Name(EventFitter.NAME)
@AutoCreate
public class EventFitter extends Fitter implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public static final String NAME = "eventFitter";
	
	private List<EventHandler> eventList;
	private EventHandler currentEvent;
	
	/**
	 * Metodo que adiciona o tratamento de eventos 
	 */
	public void addEvents() {
		ProcessDefinition processDefinition = pb.getInstance();
		for (String e : ProcessDefinition.supportedEventTypes) {
			addEvent(processDefinition, e, "br.com.infox.ibpm.util.JbpmEvents.raiseEvent(executionContext)", new Script());
		}
	}

	private void addEvent(ProcessDefinition processDefinition, String eventType, String expression, Action action) {
		Event event = processDefinition.getEvent(eventType);
		if (event == null) {
			event = new Event(eventType);
			processDefinition.addEvent(event);
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
	
	public EventHandler getCurrentEvent() {
		return currentEvent;
	}
	
	public void setCurrentEvent(EventHandler cEvent) {
		this.currentEvent = cEvent;
	}
	
	public String getEventType() {
		if (currentEvent == null) {
			return null;
		}
		return currentEvent.getEvent().getEventType();
	}
	
	public void setEventType(String type) {
		Event event = currentEvent.getEvent();
		pb.getInstance().removeEvent(event);
		ReflectionsUtil.setValue(event, "eventType", type);
		pb.getInstance().addEvent(event);
	}
	
	public List<EventHandler> getEventList() {
		if (eventList == null) {
			eventList = EventHandler.createList(pb.getInstance());
			if (eventList.size() == 1) {
				setCurrentEvent(eventList.get(0));
			}
		}
		return eventList;
	}
	
	public List<String> getSupportedEventTypes() {
		List<String> list = new ArrayList<String>();
		String[] eventTypes = pb.getInstance().getSupportedEventTypes();
		List<String> currentEvents = new ArrayList<String>();
		Collection<Event> values = pb.getInstance().getEvents().values();
		for (Event event : values) {
			currentEvents.add(event.getEventType());
		}
		for (String type : eventTypes) {
			if (!currentEvents.contains(type)) {
				list.add(type);
			}
		}
		return list;
	}
	
	@Override
	public void clear(){
		eventList = null;
		currentEvent = null;
	}
}
