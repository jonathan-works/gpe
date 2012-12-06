package br.com.infox.ibpm.entity;

import java.io.Serializable;

import javax.persistence.*;

import twitter4j.auth.AccessToken;

import br.com.infox.ibpm.entity.Usuario;

@Entity
@Table(name=ContaTwitter.NAME, schema="public")
public class ContaTwitter implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "tb_conta_twitter";
	
	private Usuario usuarioSistema;
	private long idTwitter;
	private String screenName;
	private String usuarioToken;
	private String usuarioSecretToken;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_usuario")
	public Usuario getUsuarioSistema() {
		return usuarioSistema;
	}
	public void setUsuarioSistema(Usuario usuarioSistema) {
		this.usuarioSistema = usuarioSistema;
	}
	
	@Id
	@Column(name="id_twitter", nullable=false, unique=true)
	public long getIdTwitter() {
		return idTwitter;
	}
	public void setIdTwitter(long idTwitter) {
		this.idTwitter = idTwitter;
	}
	
	@Column(name="ds_screen_name", nullable=false)
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	
	@Column(name="ds_token")
	public String getUsuarioToken() {
		return usuarioToken;
	}
	public void setUsuarioToken(String usuarioToken) {
		this.usuarioToken = usuarioToken;
	}
	
	@Column(name="ds_secret_token")
	public String getUsuarioSecretToken() {
		return usuarioSecretToken;
	}
	public void setUsuarioSecretToken(String usuarioSecretToken) {
		this.usuarioSecretToken = usuarioSecretToken;
	}
	
	@Transient
	public AccessToken getAccessToken(){
		return new AccessToken(usuarioToken, usuarioSecretToken);
	}
	
	public void setAccessToken(AccessToken accessToken){
		idTwitter = accessToken.getUserId();
		screenName = accessToken.getScreenName();
		usuarioToken = accessToken.getToken();
		usuarioSecretToken = accessToken.getTokenSecret();
	}
}
