package br.com.infox.ibpm.jpdl;

import static br.com.infox.constants.WarningConstants.RAWTYPES;
import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.JbpmException;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.action.ActionTypes;
import org.jbpm.graph.action.Script;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.GraphElement;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.ProcessFactory;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.instantiation.Delegation;
import org.jbpm.jpdl.JpdlException;
import org.jbpm.scheduler.def.CancelTimerAction;
import org.jbpm.scheduler.def.CreateTimerAction;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;

import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.ibpm.node.DecisionNode;

public class JpdlXmlWriter {

    private static final LogProvider LOG = Logging.getLogProvider(JpdlXmlWriter.class);

    private static final String DEFAULT_ENCODING = "ISO-8859-1";
    private static final int DEFAULT_IDENT_SIZE = 4;
    private static final String ELEMENT_NAME = "name";
    static final String JPDL_NAMESPACE = "urn:jbpm.org:jpdl-3.2";
    static final Namespace JBPM_NAMESPACE = new Namespace(null, JPDL_NAMESPACE);

    private Writer writer = null;
    private List<String> problems = new ArrayList<String>();
    private boolean useNamespace = true;
    private Map<String, Action> timers;

    public JpdlXmlWriter(Writer writer) {
        if (writer == null) {
            throw new JbpmException("writer is null");
        }
        this.writer = writer;
    }

    public void addProblem(String msg) {
        problems.add(msg);
    }

    public static String toString(ProcessDefinition processDefinition) {
        StringWriter stringWriter = new StringWriter();
        JpdlXmlWriter jpdlWriter = new JpdlXmlWriter(stringWriter);
        jpdlWriter.write(processDefinition);
        return stringWriter.toString();
    }

    public void setUseNamespace(boolean useNamespace) {
        this.useNamespace = useNamespace;
    }

    public void write(ProcessDefinition processDefinition) {
        problems = new ArrayList<String>();
        if (processDefinition == null) {
            throw new JbpmException("processDefinition is null");
        }
        try {
            // collect the actions of the process definition
            // we will remove each named event action and the remaining ones
            // will be written
            // on the process definition.
            // create a dom4j dom-tree for the process definition
            Document document = createDomTree(processDefinition);

            // write the dom-tree to the given writer
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            outputFormat.setIndentSize(DEFAULT_IDENT_SIZE);
            outputFormat.setEncoding(DEFAULT_ENCODING);
            XMLWriter xmlWriter = new XMLWriter(writer, outputFormat);
            xmlWriter.write(document);
            xmlWriter.flush();
            writer.flush();
        } catch (IOException e) {
            LOG.error(".write(processDefinition)", e);
            addProblem("couldn't write process definition xml: "
                    + e.getMessage());
        }

        if (problems.size() > 0) {
            throw new JpdlException(problems);
        }
    }

    @SuppressWarnings(UNCHECKED)
    private Document createDomTree(ProcessDefinition processDefinition) {
        Document document = DocumentHelper.createDocument();
        Element root = null;

        if (useNamespace) {
            root = document.addElement("process-definition", JBPM_NAMESPACE.getURI());
        } else {
            root = document.addElement("process-definition");
        }
        addAttribute(root, ELEMENT_NAME, processDefinition.getName());

        writeDescription(root, processDefinition.getDescription());

        Map<String, Swimlane> swimlanes = processDefinition.getTaskMgmtDefinition().getSwimlanes();
        if (swimlanes != null && !swimlanes.isEmpty()) {
            writeComment(root, "SWIMLANES");
            writeSwimlanes(root, processDefinition);
        }

        // write the start-state
        if (processDefinition.getStartState() != null) {
            writeComment(root, "START-STATE");
            writeStartNode(root, (StartState) processDefinition.getStartState());
        }
        // write the nodeMap
        if ((processDefinition.getNodes() != null)
                && (processDefinition.getNodes().size() > 0)) {
            writeComment(root, "NODES");
            writeNodes(root, processDefinition.getNodes());
        }
        // write the process level actions
        if (processDefinition.hasEvents()) {
            writeComment(root, "PROCESS-EVENTS");
            writeEvents(root, processDefinition);
        }
        if (processDefinition.hasActions()) {
            writeComment(root, "ACTIONS");
            List<Action> namedProcessActions = getNamedProcessActions(processDefinition.getActions());
            writeActions(root, namedProcessActions);
        }

        root.addText(System.getProperty("line.separator"));

        return document;
    }

    private void writeDescription(Element element, String text) {
        if (text == null) {
            return;
        }
        Element e = addElement(element, "description");
        e.addCDATA(text);
    }

    @SuppressWarnings(UNCHECKED)
    private void writeSwimlanes(Element root,
            ProcessDefinition processDefinition) {
        Map<String, Swimlane> swimlanes = processDefinition.getTaskMgmtDefinition().getSwimlanes();
        for (Entry<String, Swimlane> e : swimlanes.entrySet()) {
            Element swimlane = addElement(root, "swimlane");
            addAttribute(swimlane, ELEMENT_NAME, e.getKey());
            Swimlane s = e.getValue();
            if (s.getPooledActorsExpression() != null
                    || s.getActorIdExpression() != null) {
                Element assignment = addElement(swimlane, "assignment");
                addAttribute(assignment, "pooled-actors", s.getPooledActorsExpression());
                addAttribute(assignment, "actor-id", s.getActorIdExpression());
            }

        }
    }

    private List<Action> getNamedProcessActions(Map<String, Action> actions) {
        List<Action> namedProcessActions = new ArrayList<Action>();
        Iterator<Action> iter = actions.values().iterator();
        while (iter.hasNext()) {
            Action action = iter.next();
            if ((action.getEvent() == null) && (action.getName() != null)) {
                namedProcessActions.add(action);
            }
        }
        return namedProcessActions;
    }

    private Element writeStartNode(Element element, StartState startState) {
        Element newElement = null;
        if (startState != null) {
            newElement = addElement(element, getTypeName(startState));
            Task startTask = startState.getProcessDefinition().getTaskMgmtDefinition().getStartTask();
            if (startTask != null) {
                Set<Task> tasks = new HashSet<Task>();
                tasks.add(startTask);
                writeTasks(tasks, newElement);
            }
            writeNode(newElement, startState);

        }
        return newElement;
    }

    @SuppressWarnings(UNCHECKED)
    private void writeNodes(Element parentElement,
            List<org.jbpm.graph.def.Node> nodes) {
        Iterator<org.jbpm.graph.def.Node> iter = nodes.iterator();
        while (iter.hasNext()) {
            org.jbpm.graph.def.Node node = iter.next();
            if (!(node instanceof StartState)) {
                Element nodeElement = addElement(parentElement, ProcessFactory.getTypeName(node));
                if (node instanceof TaskNode) {
                    TaskNode taskNode = (TaskNode) node;
                    if (taskNode.isEndTasks()) {
                        addAttribute(nodeElement, "end-tasks", "true");
                    }
                    if (taskNode.getTasks() != null) {
                        writeTasks(taskNode.getTasks(), nodeElement);
                    }
                }
                if (node instanceof ProcessState) {
                    writeProcessState((ProcessState) node, nodeElement);
                }
                if (node instanceof DecisionNode) {
                    DecisionNode decision = (DecisionNode) node;
                    addAttribute(nodeElement, "expression", decision.getDecisionExpression());
                }
                node.write(nodeElement);
                writeNode(nodeElement, node);
            }
        }
    }

    @SuppressWarnings({ UNCHECKED, RAWTYPES })
    private void writeProcessState(ProcessState node, Element nodeElement) {
        Element subProcess = addElement(nodeElement, "sub-process");
        String subProcessName;
        if (node.getSubProcessDefinition() != null) { // Importação
            subProcessName = node.getSubProcessDefinition().getName();
        } else { // Publicação
            subProcessName = node.getSubProcessName();
        }
        subProcess.addAttribute(ELEMENT_NAME, subProcessName);
        subProcess.addAttribute("binding", "late");
        Set variableAccess = (Set) ReflectionsUtil.getValue(node, "variableAccesses");
        writeVariables(nodeElement, variableAccess);
    }

    private void writeTasks(Set<Task> tasks, Element element) {
        for (Task task : tasks) {
            Element taskElement = addElement(element, "task");
            addAttribute(taskElement, ELEMENT_NAME, task.getName());
            if (task.getSwimlane() != null) {
                addAttribute(taskElement, "swimlane", task.getSwimlane().getName());
            }
            addAttribute(taskElement, "actor-id", task.getActorIdExpression());
            addAttribute(taskElement, "condition", task.getCondition());
            addAttribute(taskElement, "description", task.getDescription());
            addAttribute(taskElement, "due-date", task.getDueDate());
            addAttribute(taskElement, "pooled-actors", task.getPooledActorsExpression());
            writeController(task.getTaskController(), taskElement);
            writeEvents(taskElement, task);
        }
    }

	@SuppressWarnings(UNCHECKED)
    private void writeController(TaskController taskController,
            Element taskElement) {
        if (taskController != null) {
            Element controller = addElement(taskElement, "controller");
            List<VariableAccess> list = taskController.getVariableAccesses();
            writeVariables(controller, list);
        }
    }

    private void writeVariables(Element controller,
            Collection<VariableAccess> list) {
        if (list == null) {
            return;
        }
        for (VariableAccess va : list) {
            Element ve = addElement(controller, "variable");
            addAttribute(ve, ELEMENT_NAME, va.getVariableName());
            addAttribute(ve, "mapped-name", va.getMappedName());
            if (va.getAccess().toString().trim().length() > 0) {
                addAttribute(ve, "access", va.getAccess().toString());
            }
        }
    }

    private void writeNode(Element element, org.jbpm.graph.def.Node node) {
        timers = new HashMap<>();
        if (node.getDescription() != null) {
            Element description = addElement(element, "description");
            description.addCDATA(node.getDescription());
        }
        addAttribute(element, ELEMENT_NAME, node.getName());
        if (node.isAsync()) {
            addAttribute(element, "async", "true");
        }
        if (node instanceof TaskNode) {
            TaskNode t = (TaskNode) node;
            String signal = null;
            switch (t.getSignal()) {
                case TaskNode.SIGNAL_FIRST:
                    signal = "first";
                break;

                case TaskNode.SIGNAL_FIRST_WAIT:
                    signal = "first-wait";
                break;

                case TaskNode.SIGNAL_LAST_WAIT:
                    signal = "last-wait";
                break;

                case TaskNode.SIGNAL_NEVER:
                    signal = "never";
                break;

                case TaskNode.SIGNAL_UNSYNCHRONIZED:
                    signal = "unsynchronized";
                break;
            }
            if (signal != null) {
                addAttribute(element, "signal", signal);
            }
        }
        writeTransitions(element, node);
        writeEvents(element, node);
    }

    @SuppressWarnings(UNCHECKED)
    private void writeTransitions(Element element, org.jbpm.graph.def.Node node) {
        if (node.getLeavingTransitionsMap() != null) {
            Iterator<Transition> iter = node.getLeavingTransitionsList().iterator();
            while (iter.hasNext()) {
                Transition transition = iter.next();
                writeTransition(element.addElement("transition"), transition);
            }
        }
    }

    @SuppressWarnings(UNCHECKED)
    private void writeTransition(Element transitionElement,
            Transition transition) {
        if (transition.getTo() != null) {
            transitionElement.addAttribute("to", transition.getTo().getName());
        }
        if (transition.getName() != null) {
            transitionElement.addAttribute(ELEMENT_NAME, transition.getName());
        }
        Event transitionEvent = transition.getEvent(Event.EVENTTYPE_TRANSITION);
        if ((transitionEvent != null) && (transitionEvent.hasActions())) {
            writeActions(transitionElement, transitionEvent.getActions());
        }
        if (transition.getCondition() != null
                && !"".equals(transition.getCondition().trim())) {
            Element condition = transitionElement.addElement("condition");
            condition.addAttribute("expression", transition.getCondition());
        }
        if (transition.getDescription() != null) {
            writeDescription(transitionElement, transition.getDescription());
        }
    }

    @SuppressWarnings(UNCHECKED)
    private void writeEvents(Element element, GraphElement graphElement) {
        if (graphElement.hasEvents()) {
            Iterator<Event> iter = graphElement.getEvents().values().iterator();
            while (iter.hasNext()) {
                Event event = iter.next();
                writeEvent(element.addElement("event"), event);
            }
        }
    }

    @SuppressWarnings(UNCHECKED)
    private void writeEvent(Element eventElement, Event event) {
        boolean valid = false;
        eventElement.addAttribute("type", event.getEventType());
        if (event.hasActions()) {
            Iterator<Action> actionIter = event.getActions().iterator();
            while (actionIter.hasNext()) {
            	Action action = actionIter.next();
            	Delegation actionDelegation = action.getActionDelegation();
                if (actionDelegation != null) {
                	valid |= writeStatusProcessoAction(eventElement, action);
                } else {
                	valid |= writeAction(eventElement, action);
                }
            }
        }
        if (!valid) {
            eventElement.detach();
        }
    }

    private boolean writeStatusProcessoAction(Element eventElement, Action action) {
    	boolean valid = false;

    	Element actionElement = addElement(eventElement, "action");
    	Delegation actionDelegation = action.getActionDelegation();
    	if (action.getName() != null && actionDelegation.getClassName() != null &&
    			actionDelegation.getConfiguration() != null) { 
	    	addAttribute(actionElement, "name", action.getName());
			addAttribute(actionElement, "class", actionDelegation.getClassName());
	    	addAttribute(actionElement, "async", "true");

	    	String configuration = actionDelegation.getConfiguration();
	    	Pattern pattern = Pattern.compile("(<statusProcesso>\\d+</statusProcesso>)");
			Matcher matcher = pattern.matcher(configuration);
			if (matcher.find()) {
				configuration = matcher.group(1);
			}
			actionElement.addCDATA(configuration);
	    	
	    	valid = true;
    	}
		return valid;
	}

	private void writeActions(Element parentElement, List<Action> actions) {
        Iterator<Action> actionIter = actions.iterator();
        while (actionIter.hasNext()) {
            Action action = actionIter.next();
            writeAction(parentElement, action);
        }
    }

    private boolean writeAction(Element parentElement, Action action) {
        boolean valid = false;
        if (writeTimer(parentElement, action)) {
            return false;
        }
        
        String actionName = ActionTypes.getActionName(action.getClass());
        Element actionElement = parentElement.addElement(actionName);

        if (action.getName() != null) {
            actionElement.addAttribute(ELEMENT_NAME, action.getName());
        }

        if (!action.acceptsPropagatedEvents()) {
            actionElement.addAttribute("accept-propagated-events", "false");
        }
        String actionExpression = action.getActionExpression();
        if (actionExpression != null) {
            actionElement.addAttribute("expression", actionExpression);
            valid = true;
        }
        action.write(actionElement);
        if (action instanceof Script) {
            Script script = (Script) action;
            String expression = script.getExpression();
            if (expression != null && !"".equals(expression.trim())) {
                actionElement.addText(expression);
                valid = true;
            }
        }
        return valid;
    }

    private void initCreateTimerAction(final Element parentElement, final Action action) {
        final CreateTimerAction create = (CreateTimerAction) action;
        final String name = create.getTimerName();
        
        final Action firstTimer = timers.get(name);
        
        if (firstTimer == null) {
            timers.put(name, action);
        }
        
        final Element node = parentElement.getParent();
        final Element timer = addElement(node, "timer");
        timer.addAttribute(ELEMENT_NAME, name);
        timer.addAttribute("duedate", create.getDueDate());
        timer.addAttribute("repeat", create.getRepeat());
        timer.addAttribute("transition", create.getTransitionName());
    }
    
    private void initCancelTimerAction(final Element parentElement, final Action action) {
        final CancelTimerAction timer = (CancelTimerAction) action;
        final String name = timer.getTimerName();
        final Action firstTimer = timers.get(name);
        
        if (firstTimer == null) {
            timers.put(name, action);
        }
    }
    
    private boolean writeTimer(Element parentElement, Action action) {
        if (action instanceof CreateTimerAction) {
            initCreateTimerAction(parentElement, action);
            return true;
        } else if (action instanceof CancelTimerAction) {
            initCancelTimerAction(parentElement, action);
        }
        
        return false;
    }

    private void writeComment(Element element, String comment) {
        element.addText(System.getProperty("line.separator"));
        element.addComment(" " + comment + " ");
    }

    private Element addElement(Element element, String elementName) {
        return element.addElement(elementName);
    }

    private void addAttribute(Element e, String attributeName, String value) {
        if (value != null) {
            e.addAttribute(attributeName, value);
        }
    }

    private String getTypeName(Object o) {
        return ProcessFactory.getTypeName((org.jbpm.graph.def.Node) o);
    }

}
