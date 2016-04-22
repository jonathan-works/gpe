package br.com.infox.epp.login;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="tb_login_invalido")
public class LoginInvalido {
	
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "LoginInvalidoGenerator", sequenceName = "sq_login_invalido")
	@GeneratedValue(generator = "LoginInvalidoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_login_invalido", nullable = false, unique = true)
	private int id;
	
	@ManyToOne
	@JoinColumn(name="id_cookie_captcha")
	private CookieCaptcha cookieCaptcha;
	
	@Column(name="dt_login")
	private Date data;

	public CookieCaptcha getCookieCaptcha() {
		return cookieCaptcha;
	}

	public void setCookieCaptcha(CookieCaptcha cookieCaptcha) {
		this.cookieCaptcha = cookieCaptcha;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getId() {
		return id;
	}
	

}
