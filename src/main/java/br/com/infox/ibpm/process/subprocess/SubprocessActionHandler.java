package br.com.infox.ibpm.process.subprocess;

import java.util.Map;

import javax.enterprise.event.Observes;

import org.jbpm.activity.exe.ActivityBehavior;
import org.jbpm.activity.exe.LoopActivityBehavior;
import org.jbpm.activity.exe.MultiInstanceActivityBehavior;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.node.Activity;

import br.com.infox.bpm.cdi.qualifier.Events.SubProcessCreated;
import br.com.infox.bpm.cdi.qualifier.Events.SubProcessEnd;
import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.seam.exception.ApplicationException;

public class SubprocessActionHandler {

    public void copyVariablesToSubprocess(@Observes @SubProcessCreated ExecutionContext executionContext) {
        try {
        	Map<String, Object> variables = executionContext.getContextInstance().getVariables();
        	removeSystemVariables(executionContext, variables);
        	ProcessInstance subProcessInstance = executionContext.getToken().getSubProcessInstance();
        	subProcessInstance.getContextInstance().addVariables(variables);
        } catch (Exception ex) {
            throw new ApplicationException(ApplicationException.createMessage("copiar variaveis para o subprocesso", "copyVariablesToSubprocess()", "SubprocessoActionHandler", "BPM"), ex);
        }
    }

    public void copyVariablesFromSubprocess(@Observes @SubProcessEnd ExecutionContext executionContext) {
        try {
            ProcessInstance subProcessInstance = executionContext.getSubProcessInstance();
        	Map<String, Object> variables = subProcessInstance.getContextInstance().getVariables();
        	removeSystemVariables(executionContext, variables);
            executionContext.getProcessInstance().getContextInstance().addVariables(variables);
        } catch (Exception ex) {
            throw new ApplicationException(ApplicationException.createMessage("copiar as variaveis do subprocesso", "copyVariablesFromSubprocess()", "SubprocessoActionHandler", "BPM"), ex);
        }
    }
    
    private void removeSystemVariables(ExecutionContext executionContext, Map<String, Object> variables) {
        Node node = (Node) HibernateUtil.removeProxy(executionContext.getToken().getNode());
        Activity activity = (Activity) node;
        ActivityBehavior activityBehavior = activity.getActivityBehavior();
        if (activityBehavior != null) {
            for (String variableName : MultiInstanceActivityBehavior.VARIABLES) {
                variables.remove(variableName);
            }
            for (String variableName : LoopActivityBehavior.VARIABLES) {
                variables.remove(variableName);
            }
            if (activityBehavior instanceof MultiInstanceActivityBehavior) {
                MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior) activityBehavior;
                if (multiInstanceActivityBehavior.getInputDataItem() != null) {
                    variables.remove(multiInstanceActivityBehavior.getInputDataItem());
                }
            }
        }
    }

}
