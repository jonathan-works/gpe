package br.com.infox.ibpm.node.handler;

import static br.com.infox.epp.processo.status.entity.StatusProcesso.STATUS_PROCESSO_ACTION_NAME;
import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;

import org.apache.commons.lang3.tuple.Pair;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.activity.exe.ActivityBehavior;
import org.jbpm.activity.exe.MultiInstanceActivityBehavior;
import org.jbpm.activity.exe.MultiInstanceActivityBehavior.EventBehavior;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.Activity;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.instantiation.Delegation;
import org.jbpm.scheduler.def.CancelTimerAction;
import org.jbpm.scheduler.def.CreateTimerAction;
import org.jbpm.taskmgmt.def.Task;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.documento.modelo.ModeloDocumentoSearch;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.manager.StatusProcessoSearch;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.task.handler.GenerateDocumentoHandler;
import br.com.infox.ibpm.task.handler.GenerateDocumentoHandler.GenerateDocumentoConfiguration;
import br.com.infox.ibpm.task.handler.StatusHandler;
import br.com.infox.ibpm.task.handler.TaskHandlerVisitor;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.jbpm.event.EventHandler;
import br.com.infox.seam.util.ComponentUtil;

public class NodeHandler implements Serializable {

    private static final String BASE_DUE_DATE = "1 business hour";
    private static final long serialVersionUID = -236376783694756255L;
    public static final String GENERATE_DOCUMENTO_ACTION_NAME = "generateDocumentoAction";
    private static final LogProvider LOG = Logging.getLogProvider(NodeHandler.class);

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
    
    public enum DueDateType {
        
        EXPRESSION("Expressão"), DATE("Data"), PERIOD("Período");
        
        private String label;
        
        private DueDateType(String label) {
            this.label = label;
        }
        
        public String getLabel() {
            return label;
        }
        
        public static DueDateType[] orderedValues() {
            return new DueDateType[]{DATE, EXPRESSION, PERIOD};
        }
        
        public static DueDateType toDueDateType(String dueDate) {
            if (StringUtil.isEmpty(dueDate)) return DATE;
            if (dueDate.startsWith("#{") || dueDate.startsWith("${")){
                return EXPRESSION;
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat(CreateTimerAction.DUEDATE_FORMAT);
                try {
                    sdf.parse(dueDate);
                    return DATE;
                } catch (ParseException e) {
                    return PERIOD;
                }
            }
        }
        
        public boolean isDate() {
            return this == DATE;
        }
        
        public boolean isExpression() {
            return this == EXPRESSION;
        }
        
        public boolean isPeriod() {
            return this == PERIOD;
        }
        
    }

    private Node node;
    private List<EventHandler> eventList;
    private EventHandler currentEvent;
    private List<CreateTimerAction> timerList = new ArrayList<>();
    private CreateTimerAction currentTimer;
    private DueDateType dueDateType;
    private Date dueDateDate;
    private String dueDateExpression;
    private String dueDateValue;
    private UnitsEnum dueDateUnit;
    private boolean dueDateBusiness;
    private StatusProcesso statusProcesso;
    private ModeloDocumento modeloDocumento;
    private ClassificacaoDocumento classificacaoDocumento;
    private String codigoPasta;
	private EventHandler multiInstanceEvent;
	private ActivityNodeType activityNodeType;
	private List<Pair<String, Pair<VariableType, Boolean>>> startVariablesSubProcess;

    public NodeHandler(Node node) {
        this.node = node;
        if (node != null) {
            loadTimers(node);
            loadEvents(node, Event.EVENTTYPE_NODE_ENTER);
            loadEvents(node, Event.EVENTTYPE_NODE_LEAVE);
        }
    }
    
    private void loadEvents(Node node, String eventType) {
    	if (node.hasEvent(eventType)) {
            Event event = node.getEvent(eventType);
            List<?> actions = event.getActions();
            for (Object object : actions) {
                Action action = (Action) object;
                Delegation actionDelegation = action.getActionDelegation();
                if (actionDelegation != null) {
                	if (StatusHandler.class.getName().equals(actionDelegation.getClassName())) {
                        String configuration = actionDelegation.getConfiguration();
                        Pattern pattern = Pattern.compile("<statusProcesso>(.+?)</statusProcesso>");
                        Matcher matcher = pattern.matcher(configuration);
                        if (matcher.find()) {
                            String status = matcher.group(1);
                            StatusProcessoSearch search = Beans.getReference(StatusProcessoSearch.class);
                            this.statusProcesso = search.getStatusByName(status);
                        }
                    } else if (GenerateDocumentoHandler.class.getName().equals(actionDelegation.getClassName())) {
                    	String configuration = new GenerateDocumentoHandler().parseJbpmConfiguration(actionDelegation.getConfiguration());
                    	try {
                    		GenerateDocumentoConfiguration generateDocumentoConfiguration = new Gson().fromJson(configuration, GenerateDocumentoConfiguration.class);
                    		ModeloDocumentoSearch modeloDocumentoSearch = Beans.getReference(ModeloDocumentoSearch.class);
                            ClassificacaoDocumentoManager classificacaoDocumentoManager = ComponentUtil.getComponent(ClassificacaoDocumentoManager.NAME);
                            this.modeloDocumento = modeloDocumentoSearch.getModeloDocumentoByCodigo(generateDocumentoConfiguration.getCodigoModeloDocumento());
                            this.classificacaoDocumento = classificacaoDocumentoManager.findByCodigo(generateDocumentoConfiguration.getCodigoClassificacaoDocumento());
                        	this.codigoPasta = generateDocumentoConfiguration.getCodigoPasta();
                        } catch (JsonSyntaxException e) {
                        	LOG.warn("Erro ao ler configuração da action GenerateDocumento no nó " + this.node.getName(), e);
                        }
                    }
                }
            }
        }
    }

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
    	if (e.getEvent().getEventType().equals(Event.EVENTTYPE_NODE_LEAVE)) {
    		setClassificacaoDocumento(null);
    		setModeloDocumento(null);
    	}
        node.removeEvent(e.getEvent());
        if (eventList != null) {
        	eventList.remove(e);
        }
        currentEvent = null;
    }

    public void addEvent() {
        Event event = new Event("new-event");
        currentEvent = new EventHandler(event);
        if (eventList == null) {
            eventList = new ArrayList<>();
        }
        eventList.add(currentEvent);
        node.addEvent(event);
        if (isSystemNode()) {
            setEventType(Event.EVENTTYPE_NODE_LEAVE);
        }
    }

    public boolean isSystemNode() {
        return node.getClass().equals(Node.class);
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

    public List<String> getSupportedEventTypes() {
        List<String> list = new ArrayList<>();
        if (!isSystemNode()) {
            list.addAll(Arrays.asList(node.getSupportedEventTypes()));
            if (node.getClass().equals(TaskNode.class)) {
                list.addAll(Arrays.asList(new Task().getSupportedEventTypes()));
            }
            for (Event event : node.getEvents().values()) {
                if (list.contains(event.getEventType()) && !EventHandler.hasOnlyTimers(event)) {
                    list.remove(event.getEventType());
                }
            }
        } else {
            list.add(Event.EVENTTYPE_NODE_LEAVE);
        }
        return list;
    }

    private void setInternalCurrentTimer(CreateTimerAction currentTimer) {
        this.currentTimer = currentTimer;
        dueDateBusiness = false;
        dueDateValue = null;
        dueDateUnit = null;
        dueDateType = DueDateType.toDueDateType(currentTimer.getDueDate());
        setDueDate(currentTimer.getDueDate());
    }

    public void setCurrentTimer(CreateTimerAction currentTimer) {
        setInternalCurrentTimer(currentTimer);
    }

    private void setDueDate(String dueDate) {
        if (dueDate == null) return;
        if (getDueDateType() == DueDateType.PERIOD) {
            dueDateBusiness = dueDate.indexOf(" business ") > -1;
            String[] s = dueDate.split(" ");
            dueDateValue = s[0];
            String unit = s[1];
            if (s.length > 2) {
                unit = s[2];
            }
            dueDateUnit = UnitsEnum.valueOf(unit);
        } else if (getDueDateType() == DueDateType.DATE) {
            dueDateDate = DateTime.parse(dueDate, DateTimeFormat.forPattern(CreateTimerAction.DUEDATE_FORMAT)).toDate();
        } else if (getDueDateType() == DueDateType.EXPRESSION) {
            dueDateExpression = dueDate;
        }
    }
    
    public CreateTimerAction getCurrentTimer() {
        return currentTimer;
    }
    
    public DueDateType getDueDateType() {
        return dueDateType;
    }

    public void setDueDateType(DueDateType dueDateType) {
        this.dueDateType = dueDateType;
    }
    
    public DueDateType[] getDueDateTypeValues() {
        return DueDateType.orderedValues();
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
        if (leavingTransitions != null && leavingTransitions.size() > 0) {
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
    }

    private String getGeneratedTimerName() {
        return format("{0} {1}", node.getName(), timerList.size()+1);
    }

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
    
    public Date getDueDateDate() {
        return dueDateDate;
    }

    public void setDueDateDate(Date dueDateDate) {
        this.dueDateDate = dueDateDate;
    }

    public String getDueDateExpression() {
        return dueDateExpression;
    }

    public void setDueDateExpression(String dueDateExpression) {
        this.dueDateExpression = dueDateExpression;
    }

    public void setDueDateValue(String dueDateValue) {
        this.dueDateValue = dueDateValue;
    }

    public String getDueDateValue() {
        return dueDateValue;
    }

    public void setDueDateUnit(UnitsEnum dueDateUnit) {
        this.dueDateUnit = dueDateUnit;
    }

    public UnitsEnum getDueDateUnit() {
        return dueDateUnit;
    }

    public void setDueDateBusiness(boolean dueDateBusiness) {
        this.dueDateBusiness = dueDateBusiness;
    }

    public boolean isDueDateBusiness() {
        return dueDateBusiness;
    }
    
    public void onChangeDueDateType() {
        dueDateBusiness = false;
        dueDateValue = null;
        dueDateUnit = null;
        dueDateExpression = null;
        dueDateDate = null;
    }

    public void onChangeDueDate() {
        if (getDueDateType() == DueDateType.EXPRESSION && getDueDateExpression() != null) {
            currentTimer.setDueDate(getDueDateExpression());
        } else if (getDueDateType() == DueDateType.DATE && getDueDateDate() != null) {
            currentTimer.setDueDate(new DateTime(getDueDateDate().getTime()).toString(CreateTimerAction.DUEDATE_FORMAT));
        } else {
            if (dueDateValue != null && dueDateUnit != null) {
                String dueDate = dueDateValue + (dueDateBusiness ? " business " : " ") + dueDateUnit.name().toLowerCase();
                currentTimer.setDueDate(dueDate);
            }
        }
    }

    public UnitsEnum getDueDateDefaultUnit() {
        return UnitsEnum.hour;
    }

    public List<String> getTransitions() {
        List<String> list = new ArrayList<>();
        if (node.getLeavingTransitions() != null) {
            for (Object t : node.getLeavingTransitions()) {
                list.add(((Transition) t).getName());
            }
        }
        return list;
    }
    
    public List<SelectItem> getTransitionItens() {
        List<SelectItem> selectItens = new ArrayList<>();
        if (node == null || node.getLeavingTransitions() == null) return selectItens;
        for (Transition transition : node.getLeavingTransitions()) {
        	selectItens.add(new SelectItem(transition.getKey(), transition.getName()));
        }
        return selectItens;
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
        onChangeSubProcess(subProcessName);
    }

    public String getSubProcessName() {
        return ReflectionsUtil.getStringValue(node, "subProcessName");
    }
    
    private void onChangeSubProcess(String subProcessName) {
        String processDefinitionName = getSubProcessName();
        if (StringUtil.isEmpty(processDefinitionName)) startVariablesSubProcess = Collections.emptyList();
        try{
        	ProcessDefinition processDefinition = JbpmUtil.instance().findLatestProcessDefinition(processDefinitionName);
        	Task startTask = processDefinition.getTaskMgmtDefinition().getStartTask();
        	if (startTask != null && startTask.getTaskController() != null 
        			&& startTask.getTaskController().getVariableAccesses() != null) {
        		List<Pair<String, Pair<VariableType, Boolean>>> variables = new ArrayList<>();
        		for (VariableAccess variableAccess : startTask.getTaskController().getVariableAccesses()) {
        			String type = variableAccess.getMappedName().split(":")[0];
        			variables.add(Pair.of(variableAccess.getVariableName(), Pair.of(VariableType.valueOf(type), variableAccess.isRequired())));
        		}
        		startVariablesSubProcess = variables;
        	} else {
        		startVariablesSubProcess = Collections.emptyList();
        	}
        }catch(NoResultException e){
        	FacesMessages.instance().add("Não foi possível encontrar o fluxo: " + subProcessName);
        	LOG.warn("Não foi possível encontrar o fluxo: " + subProcessName);
        	startVariablesSubProcess = Collections.emptyList();
        }
    }
    
    public List<Pair<String, Pair<VariableType, Boolean>>> getStartVariablesSubProcess() {
        if (startVariablesSubProcess == null && getSubProcessName() != null) {
            onChangeSubProcess(getSubProcessName());
        }
        return startVariablesSubProcess;
    }

    public List<String> getPreviousVariables() {
        TaskHandlerVisitor visitor = new TaskHandlerVisitor(false);
        visitor.visit(getNode());
        return visitor.getVariables();
    }
    
    public StatusProcesso getStatusProcesso() {
        return statusProcesso;
    }
    
    public void setStatusProcesso(StatusProcesso statusProcesso) {
        Action action = null;
        if (this.node.hasEvent(Event.EVENTTYPE_NODE_ENTER)) {
            action = retrieveStatusProcessoEvent(this.node.getEvent(Event.EVENTTYPE_NODE_ENTER));
        }
        Event event = null;
        if (action != null) {
            Delegation actionDelegation = action.getActionDelegation();
            if (actionDelegation != null && StatusHandler.class.getName().equals(actionDelegation.getClassName())) {
                event = this.node.getEvent(Event.EVENTTYPE_NODE_ENTER);
                if (statusProcesso == null) {
                    event.removeAction(action);
                    if (action.getProcessDefinition() != null) {
                    	action.getProcessDefinition().removeAction(action);
                    }
                } else {
                    actionDelegation.setConfigType("constructor");
                    actionDelegation.setConfiguration(MessageFormat.format(
                            "<statusProcesso>{0}</statusProcesso>",
                            statusProcesso.getNome()));
                }
            }
        } else if (statusProcesso != null) {
            event = createNewStatusProcessoEvent(statusProcesso);
            this.node.addEvent(event);
        }
        
        this.statusProcesso = statusProcesso;
    }
    
    public ModeloDocumento getModeloDocumento() {
		return modeloDocumento;
	}
    
    public void setModeloDocumento(ModeloDocumento modeloDocumento) {
    	Action action = null;
        if (this.node.hasEvent(Event.EVENTTYPE_NODE_LEAVE)) {
            action = retrieveGenerateDocumentoEvent(this.node.getEvent(Event.EVENTTYPE_NODE_LEAVE));
        }
        Event event = null;
        if (action != null) {
            Delegation actionDelegation = action.getActionDelegation();
            if (actionDelegation != null && GenerateDocumentoHandler.class.getName().equals(actionDelegation.getClassName())) {
                event = this.node.getEvent(Event.EVENTTYPE_NODE_LEAVE);
                if (modeloDocumento == null) {
                    event.removeAction(action);
                    if (action.getProcessDefinition() != null) {
                    	action.getProcessDefinition().removeAction(action);
                    }
                } else {
                    actionDelegation.setConfigType("constructor");
                    GenerateDocumentoConfiguration configuration = new GenerateDocumentoConfiguration();
                    configuration.setCodigoClassificacaoDocumento(classificacaoDocumento.getCodigoDocumento());
                    configuration.setCodigoModeloDocumento(modeloDocumento.getCodigo());
                    actionDelegation.setConfiguration(new Gson().toJson(configuration));
                }
            }
        } else if (modeloDocumento != null) {
            event = createNewGenerateDocumentoEvent(modeloDocumento);
            this.node.addEvent(event);
        }
        this.eventList = null;
		this.modeloDocumento = modeloDocumento;
	}
    
    public ClassificacaoDocumento getClassificacaoDocumento() {
		return classificacaoDocumento;
	}
    
    public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
    	if (this.classificacaoDocumento == null || !this.classificacaoDocumento.equals(classificacaoDocumento)) {
    		setModeloDocumento(null);
    		setCodigoPasta(null);
    	}
    	this.classificacaoDocumento = classificacaoDocumento;
	}
    
    public String getCodigoPasta() {
		return this.codigoPasta;
	}
    
    public void setCodigoPasta(String codigoPasta) {
    	Action action = null;
    	if (codigoPasta != null){
	        if (this.node.hasEvent(Event.EVENTTYPE_NODE_LEAVE)) {
	            action = retrieveGenerateDocumentoEvent(this.node.getEvent(Event.EVENTTYPE_NODE_LEAVE));
	            Delegation actionDelegation = action.getActionDelegation();
	            if (actionDelegation != null && GenerateDocumentoHandler.class.getName().equals(actionDelegation.getClassName())) {
	            	actionDelegation.setConfigType("constructor");
	            	GenerateDocumentoConfiguration configuration = new GenerateDocumentoConfiguration();
	            	configuration.setCodigoClassificacaoDocumento(classificacaoDocumento.getCodigoDocumento());
	            	configuration.setCodigoModeloDocumento(modeloDocumento.getCodigo());
	            	configuration.setCodigoPasta(codigoPasta);
	            	actionDelegation.setConfiguration(new Gson().toJson(configuration));
	            }
	        } 
    	}
    	this.codigoPasta = codigoPasta;
	}

    private Action retrieveStatusProcessoEvent(Event event) {
        List<?> actions = event.getActions();
        Action result = null;
        if (actions != null) {
	        for (Object object : actions) {
	            Action action = (Action) object;
	            if (STATUS_PROCESSO_ACTION_NAME.equals(action.getName())) {
	                result = action;
	                break;
	            }
	        }
        }
        return result;
    }
    
    private Action retrieveGenerateDocumentoEvent(Event event) {
        List<?> actions = event.getActions();
        Action result = null;
        if (actions != null) {
	        for (Object object : actions) {
	            Action action = (Action) object;
	            if (GENERATE_DOCUMENTO_ACTION_NAME.equals(action.getName())) {
	                result = action;
	                break;
	            }
	        }
        }
        return result;
    }
    
    private Event createNewStatusProcessoEvent(StatusProcesso statusProcesso) {
        Event event;
        if (this.node.hasEvent(Event.EVENTTYPE_NODE_ENTER)) {
            event = this.node.getEvent(Event.EVENTTYPE_NODE_ENTER);
        } else {
            event = new Event(Event.EVENTTYPE_NODE_ENTER);
        }
        Action action = new Action();
        action.setName(STATUS_PROCESSO_ACTION_NAME);
        Delegation delegation = new Delegation(StatusHandler.class.getName());
        delegation.setConfigType("constructor");
        delegation.setConfiguration(MessageFormat.format(
                "<statusProcesso>{0}</statusProcesso>",
                statusProcesso.getNome()));
        action.setActionDelegation(delegation);
        event.addAction(action);
        return event;
    }
    
    private Event createNewGenerateDocumentoEvent(ModeloDocumento modeloDocumento) {
        Event event;
        if (this.node.hasEvent(Event.EVENTTYPE_NODE_LEAVE)) {
            event = this.node.getEvent(Event.EVENTTYPE_NODE_LEAVE);
        } else {
            event = new Event(Event.EVENTTYPE_NODE_LEAVE);
        }
        Action action = new Action();
        action.setName(GENERATE_DOCUMENTO_ACTION_NAME);
        Delegation delegation = new Delegation(GenerateDocumentoHandler.class.getName());
        delegation.setConfigType("constructor");
        GenerateDocumentoConfiguration configuration = new GenerateDocumentoConfiguration();
        configuration.setCodigoClassificacaoDocumento(classificacaoDocumento.getCodigoDocumento());
        configuration.setCodigoModeloDocumento(modeloDocumento.getCodigo());
        delegation.setConfiguration(new Gson().toJson(configuration));
        action.setActionDelegation(delegation);
        event.addAction(action);
        return event;
    }
    
    public boolean canRemoveEvent(EventHandler eventHandler) {
    	if (eventHandler != null) {
    		if (eventHandler.getActions() != null) {
    			return eventHandler.getActions().size() == eventHandler.getEvent().getActions().size();
    		} else {
    			return eventHandler.getEvent().getActions() == null || eventHandler.getEvent().getActions().isEmpty();
    		}
    	}
    	return true;
    }
    
    public List<String> getProcessDefinitionNames() {
        return JbpmUtil.instance().getProcessDefinitionNames();
    }
    
    public ActivityNodeType[] getActivityNodeTypes() {
        return ActivityNodeType.values();
    }
    
    public ActivityNodeType getActivityNodeType() {
        if (activityNodeType == null) {
            activityNodeType = ActivityNodeType.fromActivity(getActivityBehavior());
        }
        return activityNodeType;
    }

    public void setActivityNodeType(ActivityNodeType activityNodeType) {
        this.activityNodeType = activityNodeType;
    }
    
    public void onChangeActivityNodeType() {
        getActivity().setActivityBehavior(getActivityNodeType().createActivity());
        getActivity().setActivityBehaviorClass(null);
        getActivity().setConfiguration(null);
        if (getNode().getEvents() != null) {
            getNode().getEvents().remove(MultiInstanceActivityBehavior.NONE_EVENT_BEHAVIOR);
            getNode().getEvents().remove(MultiInstanceActivityBehavior.ONE_EVENT_BEHAVIOR);
        }
    }
    
    public boolean isActivity(){
        return (getNode() instanceof Activity);
    }
    
    public Activity getActivity() {
        return Activity.class.cast(getNode());               
    }
    
    public boolean isMultiInstance(){
        return isActivity() && getActivityBehavior() != null 
                && (getActivityBehavior() instanceof MultiInstanceActivityBehavior);
    }

    public ActivityBehavior getActivityBehavior(){
        if (isActivity() && getActivity() != null) {
            return getActivity().getActivityBehavior();
        } else {
            return null;
        }
    }
    
    public EventBehavior[] getEventBehaviors() {
        return new EventBehavior[] {EventBehavior.ALL, EventBehavior.ONE, EventBehavior.NONE};
    }
    
    public EventHandler getMultiInstanceEvent(){
    	if (!isMultiInstance()){
    		return null;
    	}
    	if (this.multiInstanceEvent == null){
	    	this.multiInstanceEvent = initializeLoopEvent();
    	}
    	return this.multiInstanceEvent;
    }
    
    public void onChangeEventBehavior() {
        MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior) getActivityBehavior();
        multiInstanceActivityBehavior.setNoneBehaviorEvent(null);
        multiInstanceActivityBehavior.setOneBehaviorEvent(null);
        if (getNode().getEvents() != null) {
            getNode().getEvents().remove(MultiInstanceActivityBehavior.NONE_EVENT_BEHAVIOR);
            getNode().getEvents().remove(MultiInstanceActivityBehavior.ONE_EVENT_BEHAVIOR);
        }
        this.multiInstanceEvent = initializeLoopEvent();
    }

	private EventHandler initializeLoopEvent() {
	    MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior) getActivityBehavior();
		switch (multiInstanceActivityBehavior.getEventBehavior()) {
		case NONE:
			if (multiInstanceActivityBehavior.getNoneBehaviorEvent() == null){
			    multiInstanceActivityBehavior.setNoneBehaviorEvent(new Event(getNode(), MultiInstanceActivityBehavior.NONE_EVENT_BEHAVIOR));
			}
			return new EventHandler(multiInstanceActivityBehavior.getNoneBehaviorEvent());
		case ONE:
			if (multiInstanceActivityBehavior.getOneBehaviorEvent() == null){
			    multiInstanceActivityBehavior.setOneBehaviorEvent(new Event(getNode(), MultiInstanceActivityBehavior.ONE_EVENT_BEHAVIOR));
			}
			return new EventHandler(multiInstanceActivityBehavior.getOneBehaviorEvent());
		default:
			break;
		}
		return null;
	}
	
}
