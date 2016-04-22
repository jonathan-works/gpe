package br.com.infox.epp.login;

import java.util.Date;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name="tb_cookie_captcha")
@Entity
public class CookieCaptcha {
	
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "CookieCaptchaGenerator", sequenceName = "sq_cookie_captcha")
	@GeneratedValue(generator = "CookieCaptchaGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_cookie_captcha", nullable = false, unique = true)
	private int id;
	

	@NotNull
	@Column(name="cd_client_id")
	private String clientId;
	
	
	@NotNull
	@Column(name="dt_criacao")
	private Date dataCriacao;
	
	@ElementCollection
	@CollectionTable(
			name="tb_usuario_cookie_captcha",
			joinColumns=@JoinColumn(name="id_cookie_captcha")
	)
	@Column(name="ds_login")
	private Set<String> usuarios; 


	public Set<String> getUsuarios() {
		return usuarios;
	}


	public void setUsuarios(Set<String> usuarios) {
		this.usuarios = usuarios;
	}


	public String getClientId() {
		return clientId;
	}


	public void setClientId(String clientId) {
		this.clientId = clientId;
	}


	public Date getDataCriacao() {
		return dataCriacao;
	}


	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}


	public int getId() {
		return id;
	}
}
