package br.com.infox.epp.ws.bean;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import br.com.infox.epp.pessoa.annotation.Cpf;

public class UsuarioPerfilBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Cpf
	@NotNull
	private String cpf;

	@NotNull
	private String papel;

	@NotNull
	private String codigoLocalizacaoEstrutura;

	@NotNull
	private String codigoLocalizacaoPerfil;

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getPapel() {
		return papel;
	}

	public void setPapel(String papel) {
		this.papel = papel;
	}

	public String getCodigoLocalizacaoEstrutura() {
		return codigoLocalizacaoEstrutura;
	}

	public void setCodigoLocalizacaoEstrutura(String codigoLocalizacaoEstrutura) {
		this.codigoLocalizacaoEstrutura = codigoLocalizacaoEstrutura;
	}
	
	public String getCodigoLocalizacaoPerfil() {
		return codigoLocalizacaoPerfil;
	}

	public void setCodigoLocalizacaoPerfil(String codigoLocalizacaoPerfil) {
		this.codigoLocalizacaoPerfil = codigoLocalizacaoPerfil;
	}

	@Override
	public String toString() {
		return "UsuarioPerfilBean [cpf=" + cpf + ", papel=" + papel + ", codigoLocalizacaoEstrutura="
				+ codigoLocalizacaoEstrutura + ", codigoLocalizacaoPerfil=" + codigoLocalizacaoPerfil + "]";
	}

}