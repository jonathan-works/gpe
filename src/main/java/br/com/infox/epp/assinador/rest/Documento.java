package br.com.infox.epp.assinador.rest;

import java.util.UUID;

public class Documento {
	
	private UUID uuid;
	private Assinatura assinatura;

	public Documento(UUID uuid, Assinatura assinatura) {
		super();
		this.uuid = uuid;
		this.assinatura = assinatura;
	}
	
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	
	public Assinatura getAssinatura() {
		return assinatura;
	}
	
	public void setAssinatura(Assinatura assinatura) {
		this.assinatura = assinatura;
	}
}
