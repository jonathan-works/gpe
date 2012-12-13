package br.com.infox.ibpm.entity;

import java.io.Serializable;

import javax.persistence.*;

import twitter4j.auth.AccessToken;

import br.com.infox.epa.type.TipoTwitterEnum;
import br.com.infox.ibpm.entity.Usuario;

@Entity
@Table(name=ContaTwitter.NAME, schema="public")
public class ContaTwitter implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "tb_conta_twitter";
	
	private long idTwitter;
	private String screenName;
	private String usuarioToken;
	private String usuarioSecretToken;
	private TipoTwitterEnum tipoTwitter;
	private Usuario usuario;
	private Localizacao localizacao;
	
	@Id
	@Column(name="id_twitter", nullable=false, unique=true)
	public Long getIdTwitter() {
		return idTwitter;
	}
	public void setIdTwitter(Long idTwitter) {
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
	
	@Column(name="tp_conta_twitter", nullable=false, columnDefinition="varchar(1)", length = 1)
	@Enumerated(EnumType.STRING)
	public TipoTwitterEnum getTipoTwitter() {
		return tipoTwitter;
	}
	public void setTipoTwitter(TipoTwitterEnum tipoTwitter) {
		this.tipoTwitter = tipoTwitter;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_usuario", nullable=true)
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_localizacao", nullable=true)
	public Localizacao getLocalizacao() {
		return localizacao;
	}
	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
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