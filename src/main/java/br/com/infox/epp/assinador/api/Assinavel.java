package br.com.infox.epp.assinador.api;

import java.util.UUID;

public class Assinavel {
	
	private UUID uuid;

	public Assinavel() {
		
	}
	
	public Assinavel(UUID uuid) {
		super();
		this.uuid = uuid;
	}
	
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
}
