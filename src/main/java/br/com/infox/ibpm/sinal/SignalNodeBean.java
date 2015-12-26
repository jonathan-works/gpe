package br.com.infox.ibpm.sinal;

import java.io.Serializable;

import com.google.gson.Gson;

public class SignalNodeBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long nodeId;
	private SignalConfigurationBean listenerConfiguration;
	
	public SignalNodeBean(Long nodeId, String jsonConfiguration) {
		this.nodeId = nodeId;
		this.listenerConfiguration = new Gson().fromJson(jsonConfiguration, SignalConfigurationBean.class);
	}
	
	public Long getNodeId() {
        return nodeId;
    }

    public SignalConfigurationBean getListenerConfiguration() {
		return listenerConfiguration;
	}
}
