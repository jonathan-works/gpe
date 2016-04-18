package br.com.infox.epp.access.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "tb_recuperacao_senha")
public class RecuperacaoSenha implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String SEQUENCE_NAME = "sq_recuperacao_senha";
	private static final String GENERATOR_NAME = "RecuperacaoSenhaGenerator";

	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME)
	@GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
	@Column(name = "id_recuperacao_senha")
	private Integer id;

	@NotNull
	@Column(name = "cd_recuperacao_senha", nullable = false)
	@Size(min = 5, max = 5)
	private String codigo;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_login", nullable = false)
	private UsuarioLogin usuarioLogin;

	@NotNull
	@Column(name = "dt_criacao", nullable = false)
	private Date dataCriacao;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public UsuarioLogin getUsuarioLogin() {
		return usuarioLogin;
	}

	public void setUsuarioLogin(UsuarioLogin usuarioLogin) {
		this.usuarioLogin = usuarioLogin;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
}
