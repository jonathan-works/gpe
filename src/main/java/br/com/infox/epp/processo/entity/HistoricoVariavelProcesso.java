package br.com.infox.epp.processo.entity;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.UsuarioLogin;

@Entity
@Table(name = HistoricoVariavelProcesso.TABLE_NAME)
public class HistoricoVariavelProcesso implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_hist_processo_variavel";
	
	@Id
	@SequenceGenerator(initialValue=1, allocationSize=1, name="GeneratorHistVariavelProcesso", sequenceName="sq_hist_processo_variavel")
	@GeneratedValue(generator = "GeneratorHistVariavelProcesso", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_hist_processo_variavel", unique = true, nullable = false)
	private Long id;
	
	@NotNull
	@Column(name = "nm_variavel_processo", nullable = false)
	private String nome;
	
	@NotNull
	@Column(name = "vl_variavel_processo", nullable = false)
	private String valor;
	
	@NotNull
	@Column(name = "ds_tipo", nullable = false)
	private Class<?> tipo;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_alteracao")
	private UsuarioLogin usuarioLogin;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_alteracao")
	private Date dataAlteracao;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_variavel_processo", nullable = false)
	private VariavelProcesso variavelProcesso;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public Class<?> getTipo() {
		return tipo;
	}

	public void setTipo(Class<?> tipo) {
		this.tipo = tipo;
	}

	public UsuarioLogin getUsuarioLogin() {
		return usuarioLogin;
	}

	public void setUsuarioLogin(UsuarioLogin usuarioLogin) {
		this.usuarioLogin = usuarioLogin;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public VariavelProcesso getVariavelProcesso() {
		return variavelProcesso;
	}

	public void setVariavelProcesso(VariavelProcesso variavelProcesso) {
		this.variavelProcesso = variavelProcesso;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof HistoricoVariavelProcesso))
			return false;
		HistoricoVariavelProcesso other = (HistoricoVariavelProcesso) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
	
}
