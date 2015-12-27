package br.com.infox.ibpm.sinal;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;

import br.com.infox.core.util.StringUtil;

public class SignalNodeBean implements Serializable {
	
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(SignalNodeBean.class.getName());

	private Long id;
	private SignalConfigurationBean listenerConfiguration;
	
	public SignalNodeBean(Long id, String jsonConfiguration) {
		this.id = id;
		this.listenerConfiguration = SignalConfigurationBean.fromJson(jsonConfiguration);
	}
	
	public Long getId() {
        return id;
    }

    public SignalConfigurationBean getListenerConfiguration() {
		return listenerConfiguration;
	}
    
    public boolean canExecute() {
        return canExecute(ExecutionContext.currentExecutionContext());
    }
    
    public boolean canExecute(ExecutionContext executionContext) {
        String condition = listenerConfiguration.getCondition();
        if (StringUtil.isEmpty(condition)) {
            return true;
        }
        try {
            return (boolean) JbpmExpressionEvaluator.evaluate(condition, executionContext, Boolean.class);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
            return false;
        }
    }
}
