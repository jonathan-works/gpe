package br.com.infox.epp.assinador.api;

import java.util.UUID;

import javax.validation.constraints.NotNull;

public class Assinatura {
	@NotNull
	private byte[] assinatura;
	
	protected Assinatura() {
		
	}
	
	public Assinatura(byte[] assinatura, String codigoPerfil, String codigoLocalizacao, UUID uuidDocumento) {
		super();
		this.assinatura = assinatura;
	}
	
	public byte[] getAssinatura() {
		return assinatura;
	}
}
