package br.com.infox.ibpm.sinal;

import java.io.Serializable;

import com.google.gson.Gson;

import br.com.infox.core.util.StringUtil;

public class SignalConfigurationBean implements Serializable {
    
	private static final long serialVersionUID = 1L;
	
	private String transitionKey;
	private String condition;
	
	public SignalConfigurationBean() {
	}
	
	public SignalConfigurationBean(String transitionKey, String condition) {
	    this.transitionKey = transitionKey;
	    this.condition = condition;
	}
	
    public String getTransitionKey() {
		return transitionKey;
	}
	
	public void setTransitionKey(String transitionKey) {
		this.transitionKey = transitionKey;
	}

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
    
    public static SignalConfigurationBean fromJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            return new Gson().fromJson(json, SignalConfigurationBean.class);
        } else {
            return new SignalConfigurationBean();
        }
    }
    
    public String toJson() {
        return new Gson().toJson(this);
    }
	
}
