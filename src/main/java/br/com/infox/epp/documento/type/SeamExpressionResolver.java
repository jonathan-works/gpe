package br.com.infox.epp.documento.type;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.jboss.seam.core.Expressions;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.cdi.config.BeanManager;
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
		EntityManager entityManager = BeanManager.INSTANCE.getReference(EntityManager.class);
		TypedQuery<TaskInstance> typedQuery = entityManager.createNamedQuery("TaskMgmtSession.findOpenTasksOfProcessInstance", TaskInstance.class);
		List<TaskInstance> list = typedQuery.setMaxResults(1).setParameter("instance", processInstance).getResultList();
		if (list != null && !list.isEmpty()) {
    		TaskInstance taskInstance = list.get(0);
    		executionContext = new ExecutionContext(taskInstance.getToken());
    		executionContext.setTaskInstance(taskInstance);
		} else {
		    // TODO não funciona com split - join
            executionContext = new ExecutionContext(processInstance.getRootToken());
        }
	}

	public SeamExpressionResolver(Long idProcessInstance) {
	    EntityManager entityManager = ComponentUtil.getComponent("entityManager");
	    String jqpl = "select ti from org.jbpm.taskmgmt.exe.TaskInstance ti inner join fetch ti.token inner join fetch ti.processInstance pi where pi.id = :idProcessInstance and ti.end is null";
	    TypedQuery<TaskInstance> typedQuery = entityManager.createQuery(jqpl, TaskInstance.class);
	    List<TaskInstance> list = typedQuery.setMaxResults(1).setParameter("idProcessInstance", idProcessInstance).getResultList();
	    if (list != null && !list.isEmpty()) {
	    TaskInstance taskInstance = list.get(0);
    	    executionContext = new ExecutionContext(taskInstance.getToken());
    	    executionContext.setTaskInstance(taskInstance);
	    } else {
	        // TODO não funciona com split - join
	        TypedQuery<Token> tokenQuery = entityManager.createQuery("select t from org.jbpm.graph.exe.Token t where t.processInstance.id = :idProcessInstance and t.parent is null", Token.class);
	        Token token = tokenQuery.setParameter("idProcessInstance", idProcessInstance).getSingleResult();
	        executionContext = new ExecutionContext(token);
	    }
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
