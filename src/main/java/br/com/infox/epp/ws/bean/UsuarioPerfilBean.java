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
	private String perfil;

	@NotNull
	private String codigoLocalizacao;

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

	public String getCodigoLocalizacao() {
		return codigoLocalizacao;
	}

	public void setCodigoLocalizacao(String codigoLocalizacao) {
		this.codigoLocalizacao = codigoLocalizacao;
	}
	
	@Override
	public String toString() {
		return "UsuarioPerfilBean [cpf=" + cpf + ", perfil=" + perfil + ", codigoLocalizacao=" + codigoLocalizacao
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cpf == null) ? 0 : cpf.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UsuarioPerfilBean other = (UsuarioPerfilBean) obj;
		if (cpf == null) {
			if (other.cpf != null)
				return false;
		} else if (!cpf.equals(other.cpf))
			return false;
		return true;
	}
	

}