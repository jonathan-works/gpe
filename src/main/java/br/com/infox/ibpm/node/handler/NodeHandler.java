package br.com.infox.ibpm.node.handler;

import static br.com.infox.constants.WarningConstants.UNCHECKED;
import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.scheduler.def.CancelTimerAction;
import org.jbpm.scheduler.def.CreateTimerAction;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.constants.WarningConstants;
import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.jbpm.event.EventHandler;

public class NodeHandler implements Serializable {

    private static final String BASE_DUE_DATE = "1 business hour";
    private static final long serialVersionUID = -236376783694756255L;

    public enum UnitsEnum {

        second("Segundo"), minute("Minuto"), hour("Hora"), day("Dia"), week(
                "Semana"), month("Mes"), year("Ano");

        private String label;

        UnitsEnum(String label) {
            this.label = label;
        }

        public String getLabel() {
            return this.label;
        }

    }

    private Node node;
    private List<EventHandler> eventList;
    private EventHandler currentEvent;
    private List<CreateTimerAction> timerList = new ArrayList<CreateTimerAction>();
    private CreateTimerAction currentTimer;
    private String dueDateValue;
    private UnitsEnum dueDateUnit;
    private boolean dueDateBusiness;

    public NodeHandler(Node node) {
        this.node = node;
        if (node != null) {
            loadTimers(node);
        }
    }

    @SuppressWarnings(UNCHECKED)
    private void loadTimers(Node node) {
        Event enter = node.getEvent(Event.EVENTTYPE_NODE_ENTER);
        if (enter != null) {
            List<Action> actions = enter.getActions();
            for (Action action : actions) {
                if (action instanceof CreateTimerAction) {
                    CreateTimerAction createTimerAction = (CreateTimerAction) action;
                    timerList.add(createTimerAction);
                    if (currentTimer == null) {
                        setInternalCurrentTimer(createTimerAction);
                    }
                }
            }
        }
    }

    public Node getNode() {
        return node;
    }

    public List<EventHandler> getEventList() {
        if (eventList == null) {
            eventList = EventHandler.createList(node);
            if (eventList != null && eventList.size() == 1) {
                setCurrentEvent(eventList.get(0));
            }
        }
        return eventList;
    }

    public EventHandler getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(EventHandler currentEvent) {
        this.currentEvent = currentEvent;
        currentEvent.getActions();
    }

    public void removeEvent(EventHandler e) {
        node.removeEvent(e.getEvent());
        eventList.remove(e);
        currentEvent = null;
    }

    public void addEvent() {
        Event event = new Event("new-event");
        currentEvent = new EventHandler(event);
        if (eventList == null) {
            eventList = new ArrayList<EventHandler>();
        }
        eventList.add(currentEvent);
        node.addEvent(event);
    }

    public String getEventType() {
        if (currentEvent == null) {
            return null;
        }
        return currentEvent.getEvent().getEventType();
    }

    public void setEventType(String type) {
        Event event = currentEvent.getEvent();
        node.removeEvent(event);
        ReflectionsUtil.setValue(event, "eventType", type);
        node.addEvent(event);
    }

    @SuppressWarnings(UNCHECKED)
    public List<String> getSupportedEventTypes() {
        List<String> list = new ArrayList<String>();
        List<String> nodeEvents = Arrays.asList(new Node().getSupportedEventTypes());
        List<String> eventTypes = new ArrayList<String>(nodeEvents);
        List<String> taskEvents = Arrays.asList(new Task().getSupportedEventTypes());
        eventTypes.addAll(new ArrayList<String>(taskEvents));
        List<String> currentEvents = new ArrayList<String>();
        Collection<Event> values = node.getEvents().values();
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

    private void setInternalCurrentTimer(CreateTimerAction currentTimer) {
        this.currentTimer = currentTimer;
        dueDateBusiness = false;
        dueDateValue = null;
        dueDateUnit = null;
        setDueDate(currentTimer.getDueDate());
    }

    public void setCurrentTimer(CreateTimerAction currentTimer) {
        setInternalCurrentTimer(currentTimer);
    }

    private void setDueDate(String dueDate) {
        if (dueDate == null) {
            return;
        }
        dueDateBusiness = dueDate.indexOf(" business ") > -1;
        String[] s = dueDate.split(" ");
        dueDateValue = s[0];
        String unit = s[1];
        if (s.length > 2) {
            unit = s[2];
        }
        dueDateUnit = UnitsEnum.valueOf(unit);
    }

    public CreateTimerAction getCurrentTimer() {
        return currentTimer;
    }

    public void setTimerList(List<CreateTimerAction> timerList) {
        this.timerList = timerList;
    }

    public List<CreateTimerAction> getTimerList() {
        return timerList;
    }

    public void addTimer() {
        final CreateTimerAction newTimer = createNewTimerAction();
        
        addTimerToEvent(newTimer);
        
        setInternalCurrentTimer(newTimer);
        timerList.add(newTimer);
    }

    private CreateTimerAction createNewTimerAction() {
        final CreateTimerAction newTimer = new CreateTimerAction();
        newTimer.setTimerName(getGeneratedTimerName());
        newTimer.setDueDate(BASE_DUE_DATE);
        final List<Transition> leavingTransitions = node.getLeavingTransitions();
        if (leavingTransitions.size() > 0) {
            newTimer.setTransitionName(leavingTransitions.get(0).getName());
        }
        return newTimer;
    }

    private void addTimerToEvent(CreateTimerAction newTimer) {
        Event e = node.getEvent(Event.EVENTTYPE_NODE_ENTER);
        if (e == null) {
            e = new Event(Event.EVENTTYPE_NODE_ENTER);
            node.addEvent(e);
        }
        e.addAction(newTimer);
        e = node.getEvent(Event.EVENTTYPE_NODE_LEAVE);
        if (e == null) {
            e = new Event(Event.EVENTTYPE_NODE_LEAVE);
            node.addEvent(e);
        }
        final CancelTimerAction c = new CancelTimerAction();
        c.setTimerName(newTimer.getTimerName());
        e.addAction(c);
    }

    private String getGeneratedTimerName() {
        return format("{0} {1}", node.getName(), timerList.size()+1);
    }

    @SuppressWarnings(WarningConstants.UNCHECKED)
    public void removeTimer(CreateTimerAction timer) {
        if (timer.equals(currentTimer)) {
            currentTimer = null;
        }
        timerList.remove(timer);
        Event e = node.getEvent(Event.EVENTTYPE_NODE_ENTER);
        e.removeAction(timer);
        e = node.getEvent(Event.EVENTTYPE_NODE_LEAVE);
        Iterator<Action> it = e.getActions().iterator();
        while (it.hasNext()) {
            Action action = it.next();
            if (action instanceof CancelTimerAction && ((CancelTimerAction) action).getTimerName().equals(timer.getTimerName())) {
                e.removeAction(action);
            }
            break;
        }
    }

    public void setDueDateValue(String dueDateValue) {
        this.dueDateValue = dueDateValue;
        setDueDate();
    }

    public String getDueDateValue() {
        return dueDateValue;
    }

    public void setDueDateUnit(UnitsEnum dueDateUnit) {
        this.dueDateUnit = dueDateUnit;
        setDueDate();
    }

    public UnitsEnum getDueDateUnit() {
        return dueDateUnit;
    }

    public void setDueDateBusiness(boolean dueDateBusiness) {
        this.dueDateBusiness = dueDateBusiness;
        setDueDate();
    }

    public boolean isDueDateBusiness() {
        return dueDateBusiness;
    }

    private void setDueDate() {
        if (dueDateValue != null && dueDateUnit != null) {
            String dueDate = dueDateValue
                    + (dueDateBusiness ? " business " : " ") +
                    // Tem que ser minúsculo por causa dos mapas businessAmounts
                    // e calendarFields da classe org.jbpm.calendar.Duration
                    dueDateUnit.name().toLowerCase();
            currentTimer.setDueDate(dueDate);
        }
    }

    public UnitsEnum getDueDateDefaultUnit() {
        return UnitsEnum.hour;
    }

    public List<String> getTransitions() {
        List<String> list = new ArrayList<String>();
        if (node.getLeavingTransitions() != null) {
            for (Object t : node.getLeavingTransitions()) {
                list.add(((Transition) t).getName());
            }
        }
        return list;
    }

    public void setTimerName(String timerName) {
        Event e = node.getEvent(Event.EVENTTYPE_NODE_LEAVE);
        for (Object a : e.getActions()) {
            if (a instanceof CancelTimerAction) {
                CancelTimerAction c = (CancelTimerAction) a;
                if (c.getTimerName() == null
                        || c.getTimerName().equals(currentTimer.getTimerName())) {
                    c.setTimerName(timerName);
                }
            }
        }
        currentTimer.setTimerName(timerName);
    }

    public String getTimerName() {
        if (currentTimer == null) {
            return null;
        }
        return currentTimer.getTimerName();
    }

    public void setSubProcessName(String subProcessName) {
        ReflectionsUtil.setValue(node, "subProcessName", subProcessName);
    }

    public String getSubProcessName() {
        return ReflectionsUtil.getStringValue(node, "subProcessName");
    }

}
