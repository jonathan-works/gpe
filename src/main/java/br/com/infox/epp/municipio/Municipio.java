package br.com.infox.epp.municipio;

import java.io.Serializable;

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

import br.com.infox.constants.LengthConstants;

@Entity
@Table(name = "tb_municipio")
public class Municipio implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "MunicipioGenerator", sequenceName = "sq_municipio")
	@GeneratedValue(generator = "MunicipioGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_municipio", unique = true, nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado", nullable = false)
	private Estado estado;
	
	@Column(name="cd_municipio")
	@Size(max = 7)
	private String codigo;
	
	@NotNull
	@Size(min = 1, max = LengthConstants.DESCRICAO_PADRAO_METADE)
	@Column(name="ds_municipio", nullable = false, length = LengthConstants.DESCRICAO_PADRAO_METADE)
	private String nome;
	
	@Column(name="in_ativo")
	private Boolean ativo;
	
	public Long getId() {
		return id;
	}
	
	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public String getCodigo() {
		return codigo;
	}
	
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public Boolean getAtivo() {
		return ativo;
	}
	
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getNome());
		sb.append(" - ");
		sb.append(getEstado().getCodigo());
		return sb.toString();
	}
}
