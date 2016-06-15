package br.com.infox.epp.assinador.rest;

import java.util.UUID;

public class Documento {
	
	private UUID uuid;
	private byte[] assinatura;
	
	public Documento(UUID uuid, byte[] assinatura) {
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
	public byte[] getAssinatura() {
		return assinatura;
	}
	public void setAssinatura(byte[] assinatura) {
		this.assinatura = assinatura;
	}	
}
