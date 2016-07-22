package br.com.infox.epp.usuarioperfil;

import javax.validation.constraints.NotNull;

import br.com.infox.epp.pessoa.annotation.Cpf;

public class UsuarioPerfilDTO {

	@Cpf(message="CPF Inválido")
	@NotNull
	private String usuario;
	
	@NotNull
	private String localizacao;
	
	@NotNull
	private String perfil;

	private boolean responsavel;

	public UsuarioPerfilDTO() {
	}
	
	public UsuarioPerfilDTO(String usuario, String perfil, String localizacao) {
		super();
		this.usuario = usuario;
		this.localizacao = localizacao;
		this.perfil = perfil;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

	public boolean isResponsavel() {
		return responsavel;
	}

	public void setResponsavel(boolean responsavel) {
		this.responsavel = responsavel;
	}
	
}
