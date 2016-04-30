package org.jbpm.loopBehavior.exe;

import static org.jbpm.loopBehavior.exe.Debugger.debug;
import static org.jbpm.loopBehavior.exe.Debugger.join;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jbpm.activity.exe.ActivityBehavior;
import org.jbpm.activity.exe.LoopActivityBehavior;
import org.jbpm.context.def.ContextDefinition;
import org.jbpm.file.def.FileDefinition;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.logging.exe.LoggingInstance;
import org.jbpm.logging.log.ProcessLog;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskMgmtDefinition;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.junit.Test;

public class LoopBehaviorStandardTest {

	private static ProcessDefinition initProcesssDefinition(ActivityBehavior activityBehavior) {
		ProcessDefinition processDefinition = ProcessDefinition.createNewProcessDefinition();
		processDefinition.addDefinition(new TaskMgmtDefinition());
		processDefinition.addDefinition(new FileDefinition());
		processDefinition.addDefinition(new ContextDefinition());
		processDefinition.setName("teste");
		StartState startNode = (StartState) processDefinition.addNode(new StartState("Início"));
		TaskNode taskNode = (TaskNode) processDefinition.addNode(new TaskNode("taskNode"));
		taskNode.setActivityBehavior(activityBehavior);
		taskNode.addTask(new Task("task1"));
		EndState endState = (EndState) processDefinition.addNode(new EndState("Término"));
		startNode.addLeavingTransition(taskNode.addArrivingTransition(new Transition(taskNode.getName())));
		taskNode.addLeavingTransition(endState.addArrivingTransition(new Transition(endState.getName())));
		return processDefinition;
	}

	private ProcessDefinition createLoopEnvironment(String loopCondition, Long loopMaximum, Boolean testBefore) {
		LoopActivityBehavior config = new LoopActivityBehavior();
		if (loopCondition != null)
			config.setLoopCondition(loopCondition);
		if (loopMaximum != null)
			config.setLoopMaximum(loopMaximum);
		if (testBefore != null)
			config.setTestBefore(testBefore);
		return initProcesssDefinition(config);
	}

	@Test
	public void testTaskNodeStandardLoop() {
		Boolean[] booleans = new Boolean[] { null, Boolean.TRUE, Boolean.FALSE };
		for (Long iterations : new Long[] { null, 0L, 1L, 2L, 4L, 8L, 16L }) {
			for (Boolean loopCondition : booleans) {
				for (Boolean testBefore : booleans) {
					if (Boolean.TRUE.equals(loopCondition) && iterations == null) {
						continue;
					}
					String baseMessage = join("LoopCondition: '#{", loopCondition, "}', Iterations: ", iterations,
							", TestBefore? ", testBefore);
					debug(baseMessage);
					ProcessDefinition processDefinition = createLoopEnvironment(
							loopCondition == null ? null : join("#{", loopCondition, "}"), iterations, testBefore);
					ProcessInstance processInstance = new ProcessInstance(processDefinition);
					Token token = processInstance.getRootToken();
					assertFalse("Process shouldn't have ended", processInstance.hasEnded());
					assertTrue("Should be on start state", token.getNode() instanceof StartState);
					token.signal();
					if (Boolean.TRUE.equals(testBefore)) {
						executeTestBefore(iterations, loopCondition, token);
					} else {
						executeTestAfter(iterations, loopCondition, token);
					}
					if (processInstance.getLoggingInstance() != null) {
						printLog(processInstance.getLoggingInstance());
					}
					assertTrue("Process should have ended " + baseMessage, processInstance.hasEnded());
					assertTrue("Should be on end state " + baseMessage, token.getNode() instanceof EndState);
				}
			}
		}
	}

	private void printLog(LoggingInstance loggingInstance) {
		for (ProcessLog processLog : loggingInstance.getLogs()) {
			debug(processLog);
		}
	}

	private boolean verify(long iteration, Long iterations, Boolean loopCondition) {
		return (iterations != null || loopCondition != null) && (iterations == null || iteration < iterations)
				&& (loopCondition == null || loopCondition);
	}

	private void executeTestAfter(Long maxIterations, Boolean loopCondition, Token token) {
		int iteration = 0;
		do {
			signalToken(token);
		} while (verify(++iteration, maxIterations, loopCondition));
	}

	private void signalToken(Token token) {
		assertFalse("Process shouldn't have ended", token.getProcessInstance().hasEnded());
		assertTrue("Should be on task node", token.getNode() instanceof TaskNode);
		if (token.getNode() instanceof TaskNode) {
			for (TaskInstance taskInstance : token.getProcessInstance().getTaskMgmtInstance().getUnfinishedTasks(token)) {
				if (!taskInstance.hasEnded()){
					taskInstance.end();
				}
			}
		}
	}

	private void executeTestBefore(Long maxIterations, Boolean loopCondition, Token token) {
		int iteration = 0;
		while (verify(iteration++, maxIterations, loopCondition)) {
			signalToken(token);
		}
	}

}
