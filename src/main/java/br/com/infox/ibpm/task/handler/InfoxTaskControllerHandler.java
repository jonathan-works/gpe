package br.com.infox.ibpm.task.handler;

import java.util.List;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.taskmgmt.def.TaskControllerHandler;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.ibpm.variable.VariableAccessHandler;

public class InfoxTaskControllerHandler implements TaskControllerHandler {
    
	private static final long serialVersionUID = 1L;

	@Override
	public void initializeTaskVariables(TaskInstance taskInstance, ContextInstance contextInstance, Token token) {
		List<VariableAccess> variableAccesses = taskInstance.getTask().getTaskController().getVariableAccesses();
		for (VariableAccess variableAccess : variableAccesses) {
			if (variableAccess.getAccess().hasAccess(VariableAccessHandler.ACCESS_VARIAVEL_INICIA_VAZIA) && variableAccess.isWritable()) {
				contextInstance.setVariable(variableAccess.getMappedName(), null);
				contextInstance.setVariable(variableAccess.getVariableName(), null);
				taskInstance.setVariableLocally(variableAccess.getMappedName(), null);
			} else if (variableAccess.isReadable()) {
			    String defaultValue = variableAccess.getValue();
                if (defaultValue != null) {
                	if (defaultValue.startsWith("#{") || defaultValue.startsWith("${")) {
                        ExecutionContext executionContext = ExecutionContext.currentExecutionContext();
                        Object evaluate = JbpmExpressionEvaluator.evaluate(defaultValue, executionContext);
                        taskInstance.setVariableLocally(variableAccess.getVariableName(), evaluate);
                	}
                    else {
                        taskInstance.setVariableLocally(variableAccess.getVariableName(), defaultValue);
                    }
			    } else {
			        taskInstance.setVariableLocally(variableAccess.getMappedName(), contextInstance.getVariable(variableAccess.getVariableName()));
			    }
			} else {
				taskInstance.setVariableLocally(variableAccess.getMappedName(), null);
			}
		}
	}

	@Override
	public void submitTaskVariables(TaskInstance taskInstance, ContextInstance contextInstance, Token token) {
		List<VariableAccess> variableAccesses = taskInstance.getTask().getTaskController().getVariableAccesses();
		for (VariableAccess variableAccess : variableAccesses) {
			if (variableAccess.isRequired() && !taskInstance.hasVariableLocally(variableAccess.getMappedName())) {
				throw new IllegalArgumentException("Variável obrigatória não preenchida: " + variableAccess.getMappedName());
			}
			if (variableAccess.isWritable()) {
				contextInstance.setVariable(variableAccess.getVariableName(), taskInstance.getVariable(variableAccess.getMappedName()), token);
			}
		}
	}
}
