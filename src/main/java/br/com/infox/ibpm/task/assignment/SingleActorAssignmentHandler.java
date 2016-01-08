package br.com.infox.ibpm.task.assignment;

import org.jbpm.context.exe.VariableContainer;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public class SingleActorAssignmentHandler implements AssignmentHandler {
	private static final LogProvider LOG = Logging.getLogProvider(SingleActorAssignmentHandler.class);
	private String ownerExpression;
	
	public SingleActorAssignmentHandler() {
	}
	public SingleActorAssignmentHandler(String configuration) {
		this.ownerExpression = configuration;
	}
	
	@Override
	public void assign(Assignable assignable, ExecutionContext executionContext) throws Exception {
		if (assignable instanceof TaskInstance){
			TaskInstance ti = (TaskInstance) assignable;
			Object owner = JbpmExpressionEvaluator.evaluate(ownerExpression, executionContext);
			ti.setAssignee(owner.toString());
		} else if (assignable instanceof SwimlaneInstance){
			SwimlaneInstance si = (SwimlaneInstance) assignable;
			LOG.warn("Tratamento de swimlane instance não implementado");
		} else {
			throw new IllegalStateException("Tipo de assignable não previsto");
		}
	}

	private static final long serialVersionUID = 1L;

}
