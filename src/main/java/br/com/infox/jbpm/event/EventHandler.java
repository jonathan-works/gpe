package br.com.infox.jbpm.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jbpm.activity.exe.MultiInstanceActivityBehavior;
import org.jbpm.graph.action.Script;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.GraphElement;
import org.jbpm.scheduler.def.CancelTimerAction;
import org.jbpm.scheduler.def.CreateTimerAction;

import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.ibpm.node.handler.NodeHandler;
import br.com.infox.jbpm.action.ActionTemplateHandler;

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
        if (expression == null && event.getActions() != null && event.getActions().size() > 0) {
            Action action = (Action) event.getActions().get(0);
            if (action instanceof Script) {
                Script s = (Script) action;
                expression = s.getExpression();
            } else {
                expression = action.getActionExpression();
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
            if (!isIgnoreEvent(event)) {
                EventHandler eh = new EventHandler(event);
            	ret.add(eh);
            }
        }
        return ret;
    }
    
    public static boolean isIgnoreEvent(Event event) {
        return event.isListener() || Event.EVENTTYPE_DISPATCHER.equals(event.getEventType()) 
                || MultiInstanceActivityBehavior.NONE_EVENT_BEHAVIOR.equals(event.getEventType()) 
                || MultiInstanceActivityBehavior.ONE_EVENT_BEHAVIOR.equals(event.getEventType());
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
        if (other.getEvent() != null) {
            return this.getEvent().getEventType().equals(other.getEvent().getEventType());
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        if (this.getEvent().getEventType() != null) {
            result = prime * result + this.getEvent().getEventType().hashCode();
        } else {
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
        if (a.getProcessDefinition() != null) {
        	a.getProcessDefinition().removeAction(a);
        }
    }

    public List<Action> getActions() {
        if (actionList == null && event.getActions() != null) {
            actionList = new ArrayList<>(event.getActions());
            if (actionList != null) {
                for (Iterator<Action> it = actionList.iterator(); it.hasNext();) {
                    Action action = it.next();
                    if (NodeHandler.GENERATE_DOCUMENTO_ACTION_NAME.equals(action.getName()) || action instanceof CancelTimerAction ||
                    		action instanceof CreateTimerAction ||
                    		StatusProcesso.STATUS_PROCESSO_ACTION_NAME.equals(action.getName())) {
                        it.remove();
                    }
                }
            }
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
