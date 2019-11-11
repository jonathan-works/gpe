package br.com.infox.epp.ws.bean;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import br.com.infox.epp.pessoa.annotation.Cpf;

public class UsuarioSenhaBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Cpf
	@NotNull
	private String cpf;
	
	@NotNull
	private String senha;
	

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [cpf=" + cpf + ", senha=" + "*omitida*" + "]";
	}
}
