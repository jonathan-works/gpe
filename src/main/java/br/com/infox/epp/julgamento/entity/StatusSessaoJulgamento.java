package br.com.infox.epp.julgamento.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tb_status_sessao_julgamento")
public class StatusSessaoJulgamento implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(initialValue=1, allocationSize=1, name="StatusSessaoGenerator", sequenceName="sq_status_sessao_julgamento")
	@GeneratedValue(generator="StatusSessaoGenerator", strategy=GenerationType.SEQUENCE)
	@Column(name = "id_status_sessao_julgamento", unique = true, nullable = false)
	private Long id;
	
	@NotNull
	@Column(name = "nm_status_sessao_julgamento", nullable = false)
	private String nome;

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

	@Override
	public String toString() {
		return nome;
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
		if (!(obj instanceof StatusSessaoJulgamento))
			return false;
		StatusSessaoJulgamento other = (StatusSessaoJulgamento) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
	
}
