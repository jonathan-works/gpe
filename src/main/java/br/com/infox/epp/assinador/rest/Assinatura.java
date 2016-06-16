package br.com.infox.epp.assinador.rest;

public class Assinatura {
	private String codigoPerfil;
	private byte[] assinatura;
	
	public Assinatura(byte[] assinatura, String codigoPerfil) {
		super();
		this.assinatura = assinatura;
		this.codigoPerfil = codigoPerfil;
	}
	
	public byte[] getAssinatura() {
		return assinatura;
	}
	public String getCodigoPerfil() {
		return codigoPerfil;
	}
}
