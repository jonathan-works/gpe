package br.com.infox.epp.assinador.api;

import javax.validation.constraints.NotNull;

public class Assinatura {
	@NotNull
	private byte[] assinatura;
	
	public Assinatura() {
	}
	
	public Assinatura(byte[] assinatura) {
		super();
		this.assinatura = assinatura;
	}
	
	public byte[] getAssinatura() {
		return assinatura;
	}
	public void setAssinatura(byte[] assinatura) {
            this.assinatura = assinatura;
        }
}
