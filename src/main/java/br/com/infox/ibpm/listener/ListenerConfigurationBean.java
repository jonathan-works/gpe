package br.com.infox.ibpm.listener;

import java.io.Serializable;

public class ListenerConfigurationBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String transitionKey;
	
	public String getTransitionKey() {
		return transitionKey;
	}
	
	public void setTransitionKey(String transitionKey) {
		this.transitionKey = transitionKey;
	}
}
