package br.com.infox.core.util;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.taskmgmt.exe.TaskInstance;

public class ELUtil {
	
	public static boolean isEL(String value) {
		return (value.startsWith("#{") || value.startsWith("${")) && value.endsWith("}");
	}
	
	public static Object evaluateJbpm(ExecutionContext ctx, String value) {
		if(!isEL(value)) {
			return value;
		}
		return JbpmExpressionEvaluator.evaluate(value, ctx);
	}
	
	public static Object evaluateJbpm(TaskInstance taskInstance, String value) {
        ExecutionContext ctx = new ExecutionContext(taskInstance.getToken());
        ctx.setTaskInstance(taskInstance);
        return evaluateJbpm(ctx, value);
	}

}
