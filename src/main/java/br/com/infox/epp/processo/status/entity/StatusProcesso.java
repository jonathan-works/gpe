package br.com.infox.epp.processo.status.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;

@Entity
@Table(name = StatusProcesso.TABLE_NAME)
public class StatusProcesso implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_status_processo";

	private Integer idStatusProcesso; 
	private String nome;

	@SequenceGenerator(allocationSize=1, initialValue=1, name="generator", sequenceName="sq_status_processo")
	@Id
	@GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_status_processo", unique = true, nullable = false)
	public Integer getIdStatusProcesso() {
		return idStatusProcesso;
	}

	public void setIdStatusProcesso(Integer idStatusProcesso) {
		this.idStatusProcesso = idStatusProcesso;
	}

	@Column(name = "nm_status_processo", nullable = false, length = LengthConstants.NOME_PADRAO)
	@NotNull
	@Size(max = LengthConstants.NOME_PADRAO)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String toString() {
		return this.nome;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((getIdStatusProcesso() == null) ? 0 : getIdStatusProcesso().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof StatusProcesso))
			return false;
		StatusProcesso other = (StatusProcesso) obj;
		if (getIdStatusProcesso() == null) {
			if (other.getIdStatusProcesso() != null)
				return false;
		} else if (!getIdStatusProcesso().equals(other.getIdStatusProcesso()))
			return false;
		return true;
	}
	
}