package org.jbpm.loopBehavior.exe;

import static org.jbpm.loopBehavior.exe.Debugger.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.jbpm.JbpmException;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.def.node.loop.LoopConfigurationMultiInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.log.NodeLog;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.instantiation.Delegation;
import org.jbpm.logging.exe.LoggingInstance;
import org.jbpm.logging.log.ProcessLog;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.util.Clock;
import org.junit.Test;

public class LoopBehaviorMultiInstanceTest {

	@Test
	public void testarTaskNodeSequentialMultiInstanceLoop() throws InterruptedException {
		ProcessDefinition processDefinition = ProcessDefinition.createNewProcessDefinition();
		processDefinition.setName("teste");
		StartState startNode = (StartState) processDefinition.addNode(new StartState("Início"));
		TaskNode taskNode = (TaskNode) processDefinition.addNode(new TaskNode("taskNode"));
		Event ev = new Event("oneBehaviorEvent");
		Action action = new Action();
		action.setActionDelegation(new Delegation(new ActionHandler() {
			private static final long serialVersionUID = 1L;
			@Override
			public void execute(ExecutionContext executionContext) throws Exception {
				Token token = executionContext.getToken();
				debug(token.getFullName());
				for (Entry<String, Object> entry : executionContext.getContextInstance().getVariables(token).entrySet()) {
					debug(entry.getKey(), entry.getValue());
				}
				Node node = token.getNode();
				Transition transition = node.getDefaultLeavingTransition();
				if (transition == null) throw new JbpmException("transition is null");

			    token.setNode(node);
			    executionContext.setTransition(transition);

			    // fire the leave-node event for this node
			    node.fireEvent(Event.EVENTTYPE_NODE_LEAVE, executionContext);

			    // log this node
			    if (token.getNodeEnter() != null) {
			      token.addLog(new NodeLog(node, token.getNodeEnter(), Clock.getCurrentTime()));
			    }

			    // update the runtime information for taking the transition
			    // the transitionSource is used to calculate events on superstates
			    executionContext.setTransitionSource(node);

			    // take the transition
			    transition.take(executionContext);
			}
		}));
		ev.addAction(action);
//		taskNode.addEvent(ev);
		LoopConfigurationMultiInstance loopCharacteristics = new LoopConfigurationMultiInstance();
		loopCharacteristics.setNoneBehaviorEvent(ev);
		loopCharacteristics.setIsSequential(Boolean.FALSE);
		loopCharacteristics.setLoopCardinality("#{9}");
		loopCharacteristics.setLoopDataInput("#{inputCollection}");
//		loopCharacteristics.setLoopDataOutput("#{outputCollection}");

		taskNode.setLoopConfiguration(loopCharacteristics);
		taskNode.addTask(new Task("task1"));
		EndState endState = (EndState) processDefinition.addNode(new EndState("Término"));
		startNode.addLeavingTransition(taskNode.addArrivingTransition(new Transition(taskNode.getName())));
		taskNode.addLeavingTransition(endState.addArrivingTransition(new Transition(endState.getName())));

		HashMap<String, Object> variables = new HashMap<String, Object>();

		variables.put("inputCollection", new Object[] { "Um", "Dois", "Tres" });
		ProcessInstance processInstance = new ProcessInstance(processDefinition, variables);
		processInstance.addInstance(new LoggingInstance());
		Token token = processInstance.getRootToken();
		int count =0;
		while (!processInstance.hasEnded()) {
			if (token.hasActiveChildren()) {
				TaskInstance ti = getUnfinishedTask(token);
				if (ti != null) {
					// ti.getContextInstance().setVariableLocally("outputDataItem",
					// MessageFormat.format("{0} -> {1}",
					// ti.getVariable("inputDataItem"), "Done"), ti.getToken());
//					ti.setVariableLocally("outputDataItem", MessageFormat.format("{0} -> {1}", ti.getVariable("inputDataItem"), "Done"));
					ti.end();
				}
			} else {
				token.signal();
			}
			for (TaskInstance taskInstance : processInstance.getTaskMgmtInstance().getTaskInstances()) {
				debug(taskInstance.getToken().getFullName(), taskInstance.getTask().getName(), "Status[", getStatus(taskInstance),"]");
			}
			Thread.sleep(1000L);
		}
//		 if (processInstance.getLoggingInstance() != null){
//			 printLog(processInstance.getLoggingInstance());
//		 }
	}

	private String getStatus(TaskInstance taskInstance) {
		if (taskInstance.isOpen())
			return "Opened";
		else if (taskInstance.isCancelled())
			return "Canceled";
		else if (taskInstance.isSuspended())
			return "Suspended";
		else if (taskInstance.hasEnded())
			return "Ended";
		else
			return "???";
	}

	private void signalChildTokens(Token token) {
		for (Token child : token.getChildren().values()) {
			if (!child.isSuspended() && child.getNode() != null) {
				child.signal();
			}
		}
	}

	private TaskInstance getUnfinishedTask(Token token) {
		TaskInstance taskInstance = null;
		Collection<TaskInstance> unfinishedTasks = token.getProcessInstance().getTaskMgmtInstance().getUnfinishedTasks(token);
		debug("Unfinished tasks size "+unfinishedTasks.size());
		if (unfinishedTasks != null && !unfinishedTasks.isEmpty()) {
			taskInstance = unfinishedTasks.iterator().next();
		}
		if (taskInstance == null) {
			Iterator<Token> iterator = token.getActiveChildren().values().iterator();
			while (taskInstance == null && iterator.hasNext()) {
				taskInstance = getUnfinishedTask(iterator.next());
			}
		}
		return taskInstance;
	}

	private void printLog(LoggingInstance loggingInstance) {
		for (ProcessLog processLog : loggingInstance.getLogs()) {
			debug(processLog);
		}
	}

}
