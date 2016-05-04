package br.com.infox.ibpm.process.subprocess;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jbpm.activity.exe.LoopActivityBehavior;
import org.jbpm.activity.exe.MultiInstanceActivityBehavior;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.seam.exception.ApplicationException;

@Name("subprocessActionHandler")
@BypassInterceptors
@Scope(ScopeType.APPLICATION)
public class SubprocessActionHandler {

    @Observer(Event.EVENTTYPE_SUBPROCESS_CREATED)
    public void copyVariablesToSubprocess(ExecutionContext executionContext) {
        try {
        	Map<String, Object> variables = executionContext.getContextInstance().getVariables();
        	removeSystemVariables(variables);
        	ProcessInstance subProcessInstance = executionContext.getToken().getSubProcessInstance();
        	subProcessInstance.getContextInstance().addVariables(variables);
        } catch (Exception ex) {
            throw new ApplicationException(ApplicationException.createMessage("copiar variaveis para o subprocesso", "copyVariablesToSubprocess()", "SubprocessoActionHandler", "BPM"), ex);
        }
    }

    @Observer(Event.EVENTTYPE_SUBPROCESS_END)
    public void copyVariablesFromSubprocess(ExecutionContext executionContext) {
        try {
            ProcessInstance subProcessInstance = executionContext.getSubProcessInstance();
        	Map<String, Object> variables = subProcessInstance.getContextInstance().getVariables();
        	removeSystemVariables(variables);
            executionContext.getProcessInstance().getContextInstance().addVariables(variables);
        } catch (Exception ex) {
            throw new ApplicationException(ApplicationException.createMessage("copiar as variaveis do subprocesso", "copyVariablesFromSubprocess()", "SubprocessoActionHandler", "BPM"), ex);
        }
    }
    
    private void removeSystemVariables(Map<String, Object> variables) {
        if (variables != null) {
            for (String variableName : MultiInstanceActivityBehavior.VARIABLES) {
                variables.remove(variableName);
            }
            for (String variableName : LoopActivityBehavior.VARIABLES) {
                variables.remove(variableName);
            }
        }
    }

}
