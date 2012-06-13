package br.com.infox.ibpm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = Status.TABLE_NAME, schema = "public")
public class Status implements java.io.Serializable {

	public static final String TABLE_NAME = "tb_status";
	private static final long serialVersionUID = 1L;

	private int idStatus;
	private String status;
	private Boolean ativo;
	private Boolean mensuravel;

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_status")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_status", unique = true, nullable = false)
	public int getIdStatus() {
		return this.idStatus;
	}

	public void setIdStatus(int idStatus) {
		this.idStatus = idStatus;
	}

	@Column(name = "ds_status", length = 100, unique = true, nullable = false)
	@Length(max = 100)
	@NotNull
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "in_ativo", nullable = false)
	@NotNull
	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	@Column(name = "in_mensuravel", nullable = false)
	@NotNull
	public Boolean getMensuravel() {
		return mensuravel;
	}
	
	public void setMensuravel(Boolean mensuravel) {
		this.mensuravel = mensuravel;
	}
	
	@Override
	public String toString() {
		return status;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Status)) {
			return false;
		}
		Status other = (Status) obj;
		if (getIdStatus() != other.getIdStatus()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdStatus();
		return result;
	}
}