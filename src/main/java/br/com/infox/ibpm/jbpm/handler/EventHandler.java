/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.jbpm.handler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.graph.action.Script;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.GraphElement;


public class EventHandler implements Serializable {

	private static final long serialVersionUID = -7904557434535614157L;
	private Event event;
	private String expression;
	private Action currentAction;
	private List<Action> actionList;
	
	public EventHandler(Event event) {
		this.event = event;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public String getExpression() {
		if (currentAction != null) {
			return currentAction.getActionExpression();
		}
		if (expression == null) {
			if (event.getActions() != null && event.getActions().size() > 0) {
				Action action = (Action) event.getActions().get(0);
				if (action instanceof Script) {
					Script s = (Script) action;
					expression = s.getExpression();
				} else {
					expression = action.getActionExpression();
				}
			}
		}
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
		if (event.getActions() == null) {
			event.addAction(new Script());
		}
		if (event.getActions().size() > 0) {
			Action action = (Action) event.getActions().get(0);
			if (action instanceof Script) {
				Script s = (Script) action;
				s.setExpression(expression);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<EventHandler> createList(GraphElement instance) {
		if (instance == null) {
			return null;
		}
		List<EventHandler> ret = new ArrayList<EventHandler>();
		Map<String, Event> events = instance.getEvents();
		if (events == null) {
			return ret;
		}
		for (Event event : events.values()) {
			EventHandler eh = new EventHandler(event);
			ret.add(eh);
		}
		return ret;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof EventHandler)) {
			return false;
		}
		EventHandler other = (EventHandler) obj;
		if(other.getEvent() != null) {
		    return this.getEvent().getEventType().equals(other.getEvent().getEventType());
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if(this.getEvent().getEventType()!= null) {
		    result = prime * result + this.getEvent().getEventType().hashCode();
		}
		else {
		    result = prime * result + this.getEvent().hashCode();
		}
		return result;
	}
	
	public void addAction() {
		event.addAction(new Action());
		actionList = null;
	}

	public void removeAction(Action a) {
		event.removeAction(a);
		actionList = null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Action> getActions() {
		if (actionList == null) {
			actionList = event.getActions();
			setCurrentAction(null);
		}
		return actionList;
	}

	public Action getCurrentAction() {
		return currentAction;
	}

	public void setCurrentAction(Action currentAction) {
		this.currentAction = currentAction;
	}
	
	public String getCurrentActionType() {
		return getIcon(currentAction);
	}

	public String getIcon(Action action) {
		if (action == null) {
			return null;
		}
		String type = "action";
		if (action instanceof Script) {
			type = "script";
		}
		return type;
	}
	
	public void setTemplate() {
		ActionTemplateHandler.instance().setCurrentActionTemplate(getExpression());
	}
}