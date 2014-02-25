package br.com.infox.ibpm.process.subprocess;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.TaskInstance;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import br.com.infox.core.exception.ApplicationException;

@Name("subprocessActionHandler")
@BypassInterceptors
@Scope(ScopeType.APPLICATION)
public class SubprocessActionHandler {

    @SuppressWarnings(UNCHECKED)
    @Observer(Event.EVENTTYPE_SUBPROCESS_CREATED)
    public void copyVariablesToSubprocess() {
        try {
            Token token = TaskInstance.instance().getToken();
            ProcessInstance subProcessInstance = token.getSubProcessInstance();
            Map<String, Object> variables = TaskInstance.instance().getVariables();
            subProcessInstance.getContextInstance().addVariables(variables);
        } catch (Exception ex) {
            throw new ApplicationException(ApplicationException.createMessage("copiar variaveis para o subprocesso", "copyVariablesToSubprocess()", "SubprocessoActionHandler", "BPM"), ex);
        }
    }

    @SuppressWarnings(UNCHECKED)
    @Observer(Event.EVENTTYPE_SUBPROCESS_END)
    public void copyVariablesFromSubprocess() {
        try {
            Token token = TaskInstance.instance().getToken();
            ProcessInstance subProcessInstance = token.getProcessInstance();
            Map<String, Object> variables = subProcessInstance.getContextInstance().getVariables();
            org.jboss.seam.bpm.ProcessInstance.instance().getContextInstance().addVariables(variables);
        } catch (Exception ex) {
            throw new ApplicationException(ApplicationException.createMessage("copiar as variaveis do subprocesso", "copyVariablesFromSubprocess()", "SubprocessoActionHandler", "BPM"), ex);
        }
    }

}
