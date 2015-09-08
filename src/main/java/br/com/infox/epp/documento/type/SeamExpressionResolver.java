package br.com.infox.epp.documento.type;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.jboss.seam.core.Expressions;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.seam.util.ComponentUtil;

public class SeamExpressionResolver implements ExpressionResolver {
	
	private ExecutionContext executionContext;
	
	public SeamExpressionResolver() {
	}
	
	public SeamExpressionResolver(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}
	
	public SeamExpressionResolver(TaskInstance taskInstance) {
		executionContext = new ExecutionContext(taskInstance.getToken());
		executionContext.setTaskInstance(taskInstance);
	}
	
	public SeamExpressionResolver(ProcessInstance processInstance) {
		EntityManager entityManager = ComponentUtil.getComponent("entityManager");
		TypedQuery<TaskInstance> typedQuery = entityManager.createNamedQuery("TaskMgmtSession.findOpenTasksOfProcessInstance", TaskInstance.class);
		List<TaskInstance> list = typedQuery.setMaxResults(1).setParameter("instance", processInstance).getResultList();
		TaskInstance taskInstance = list.get(0);
		executionContext = new ExecutionContext(taskInstance.getToken());
		executionContext.setTaskInstance(taskInstance);
	}
	
	@Override
	public Expression resolve(Expression expression) {
		Object value = null;
		if (executionContext == null) {
			value = Expressions.instance().createValueExpression(expression.getExpression()).getValue();
		} else {
			value = JbpmExpressionEvaluator.evaluate(expression.getExpression(), executionContext);
		}
		if (value != null) {
			expression.setResolved(true);
			expression.setValue(value.toString());
		}
		return expression;
	}
}
