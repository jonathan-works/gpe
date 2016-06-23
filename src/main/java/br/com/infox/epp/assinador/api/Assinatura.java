package br.com.infox.epp.assinador.api;

import java.util.UUID;

import javax.validation.constraints.NotNull;

public class Assinatura {
	private UUID uuidDocumento;
	
	@NotNull
	private String codigoPerfil;
	@NotNull
	private String codigoLocalizacao;
	
	@NotNull
	private byte[] assinatura;
	
	protected Assinatura() {
		
	}
	
	public Assinatura(byte[] assinatura, String codigoPerfil, String codigoLocalizacao, UUID uuidDocumento) {
		super();
		this.assinatura = assinatura;
		this.codigoPerfil = codigoPerfil;
		this.uuidDocumento = uuidDocumento;
	}
	
	public byte[] getAssinatura() {
		return assinatura;
	}
	public String getCodigoPerfil() {
		return codigoPerfil;
	}
	public String getCodigoLocalizacao() {
		return codigoLocalizacao;
	}
	public UUID getUuidDocumento() {
		return uuidDocumento;
	}
}
