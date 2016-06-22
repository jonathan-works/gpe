package br.com.infox.epp.assinador.api;

public class ConfiguracoesAssinador {
	
	private String url;
	private String token;
	private String codigoPerfil;
	private String codigoLocalizacao;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getCodigoPerfil() {
		return codigoPerfil;
	}
	public void setCodigoPerfil(String codigoPerfil) {
		this.codigoPerfil = codigoPerfil;
	}
	public String getCodigoLocalizacao() {
		return codigoLocalizacao;
	}
	public void setCodigoLocalizacao(String codigoLocalizacao) {
		this.codigoLocalizacao = codigoLocalizacao;
	}
}
