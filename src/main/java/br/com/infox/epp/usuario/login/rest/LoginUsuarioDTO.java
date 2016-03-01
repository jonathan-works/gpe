package br.com.infox.epp.usuario.login.rest;

public class LoginUsuarioDTO {
	private String login;
	private String senha;

	public LoginUsuarioDTO() {
	}
	public LoginUsuarioDTO(String login, String senha) {
		this.login = login;
		this.senha = senha;
	}
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

}
