package br.com.infox.ibpm.listener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Singleton
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EppJbpmListener implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Map<String, String> listeners = new HashMap<>();
	
	@PostConstruct
	private void init() {
		registerListeners();
	}
	
	protected void registerListeners() {
		
	}
	
	protected void registerListener(String name, String label) {
		listeners.put(name, label);
	}
	
	public Map<String, String> getListeners() {
		return listeners;
	}

}
