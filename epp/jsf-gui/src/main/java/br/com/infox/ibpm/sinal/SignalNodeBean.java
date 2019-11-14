package br.com.infox.ibpm.sinal;

import java.io.Serializable;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;

import br.com.infox.core.util.StringUtil;

public class SignalNodeBean implements Serializable {
	
    private static final long serialVersionUID = 1L;

	private Long id;
	private SignalConfigurationBean listenerConfiguration;
	private DispatcherConfiguration signalDispatcherConfiguration;
	
	public SignalNodeBean(Long id, String jsonConfiguration) {
		this.id = id;
		this.listenerConfiguration = SignalConfigurationBean.fromJson(jsonConfiguration);
		this.signalDispatcherConfiguration = createSignalDispatcherConfiguration();
	}
	
	public Long getId() {
        return id;
    }

    public SignalConfigurationBean getListenerConfiguration() {
		return listenerConfiguration;
	}
    
    public DispatcherConfiguration getSignalDispatcherConfiguration() {
        return signalDispatcherConfiguration;
    }

    public boolean canExecute() {
        return canExecute(ExecutionContext.currentExecutionContext());
    }
    
    public boolean canExecute(ExecutionContext executionContext) {
        String condition = listenerConfiguration.getCondition();
        if (StringUtil.isEmpty(condition)) {
            return true;
        }
        return (boolean) JbpmExpressionEvaluator.evaluate(condition, executionContext, Boolean.class);
    }
    
    private static DispatcherConfiguration createSignalDispatcherConfiguration() {
        ExecutionContext executionContext = ExecutionContext.currentExecutionContext();
        if (executionContext != null && executionContext.getEvent() != null) {
            return DispatcherConfiguration.fromJson(executionContext.getEvent().getConfiguration());
        }
        return null;
    }
}
