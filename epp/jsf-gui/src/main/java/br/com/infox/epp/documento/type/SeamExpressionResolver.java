package br.com.infox.epp.documento.type;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.jboss.seam.core.Expressions;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.cdi.producer.EntityManagerProducer;

public class SeamExpressionResolver implements ExpressionResolver {
	
	private ExecutionContext executionContext;
	
	public SeamExpressionResolver() {
	}
	
	public SeamExpressionResolver(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}
	
	public SeamExpressionResolver(TaskInstance taskInstance) {
	    Token token = getEntityManager().find(Token.class, taskInstance.getToken().getId());
	    taskInstance = getEntityManager().find(TaskInstance.class, taskInstance.getId());
	    executionContext = new ExecutionContext(token);
		executionContext.setTaskInstance(taskInstance);
	}
	
	public SeamExpressionResolver(ProcessInstance processInstance) {
		TypedQuery<TaskInstance> typedQuery = getEntityManager().createNamedQuery("TaskMgmtSession.findOpenTasksOfProcessInstance", TaskInstance.class);
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
	    String jqpl = "select ti from org.jbpm.taskmgmt.exe.TaskInstance ti inner join fetch ti.token inner join fetch ti.processInstance pi where pi.id = :idProcessInstance and ti.end is null";
	    TypedQuery<TaskInstance> typedQuery = getEntityManager().createQuery(jqpl, TaskInstance.class);
	    List<TaskInstance> list = typedQuery.setMaxResults(1).setParameter("idProcessInstance", idProcessInstance).getResultList();
	    if (list != null && !list.isEmpty()) {
	    TaskInstance taskInstance = list.get(0);
    	    executionContext = new ExecutionContext(taskInstance.getToken());
    	    executionContext.setTaskInstance(taskInstance);
	    } else {
	        // TODO não funciona com split - join
	        TypedQuery<Token> tokenQuery = getEntityManager().createQuery("select t from org.jbpm.graph.exe.Token t where t.processInstance.id = :idProcessInstance and t.parent is null", Token.class);
	        Token token = tokenQuery.setParameter("idProcessInstance", idProcessInstance).getSingleResult();
	        executionContext = new ExecutionContext(token);
	    }
	}

	@Override
	public Expression resolve(Expression expression) {
		Object value = null;
		if (executionContext != null) {
		    value = JbpmExpressionEvaluator.evaluate(expression.getExpression(), executionContext);
		} 
		if (value == null){
		    value = Expressions.instance().createValueExpression(expression.getExpression()).getValue();
		}
		if (value != null) {
			resolveAsJavaType(expression, value);
		}
		return expression;
	}
	
	
	private void resolveAsJavaType(Expression expression, Object value) {
		if (value instanceof Date) {
			expression.setValue(new SimpleDateFormat("dd/MM/yyyy").format(value));
		} else if (value instanceof Boolean) {
			expression.setValue((Boolean) value ? "Sim" : "Não");
		} else if (value instanceof Double) {
			expression.setValue(String.format(new Locale("pt", "BR"), "%."+ (BigDecimal.valueOf((Double) value).scale()) +"f", value));
		} else {
			expression.setValue(value.toString());
		}
		expression.setOriginalValue(value);
		expression.setResolved(true);
	}
	
	private EntityManager getEntityManager() {
	    return EntityManagerProducer.getEntityManager();
	}
}
