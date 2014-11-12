package br.com.infox.epp.processo.entity;

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

@Entity
@Table(name = VariavelProcesso.TABLE_NAME)
public class VariavelProcesso implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_variavel_processo";
	
	@Id
	@SequenceGenerator(initialValue=1, allocationSize=1, name="GeneratorVariavelProcesso", sequenceName="sq_variavel_processo")
	@GeneratedValue(generator = "GeneratorVariavelProcesso", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_variavel_processo", unique = true, nullable = false)
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
	@JoinColumn(name = "id_processo", nullable = false)
	private Processo processo;

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

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
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
		if (!(obj instanceof VariavelProcesso))
			return false;
		VariavelProcesso other = (VariavelProcesso) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
	
}
