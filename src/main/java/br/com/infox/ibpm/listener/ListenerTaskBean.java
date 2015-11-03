package br.com.infox.ibpm.listener;

import java.io.Serializable;

import com.google.gson.Gson;

public class ListenerTaskBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long taskInstanceId;
	private ListenerConfigurationBean listenerConfiguration;
	
	public ListenerTaskBean(Long taskInstanceId, String jsonConfiguration) {
		this.taskInstanceId = taskInstanceId;
		this.listenerConfiguration = new Gson().fromJson(jsonConfiguration, ListenerConfigurationBean.class);
	}
	
	public Long getTaskInstanceId() {
		return taskInstanceId;
	}
	
	public ListenerConfigurationBean getListenerConfiguration() {
		return listenerConfiguration;
	}
}
