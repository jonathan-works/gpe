package br.com.infox.epp.assinador.rest;

import java.util.UUID;

public class Documento {
	
	private UUID uuid;

	public Documento() {
		
	}
	
	public Documento(UUID uuid) {
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
