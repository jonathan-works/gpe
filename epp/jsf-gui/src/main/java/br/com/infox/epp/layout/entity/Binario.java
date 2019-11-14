package br.com.infox.epp.layout.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.core.persistence.ORConstants;

@Entity
@Table(name = Binario.TABLE_NAME)
public class Binario {

	public static final String TABLE_NAME = "tb_binario";
	
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = ORConstants.GENERATOR, sequenceName = "sq_binario")
	@GeneratedValue(generator = ORConstants.GENERATOR, strategy = GenerationType.SEQUENCE)
	@Column(name = "id_binario", nullable = false, unique = true)
	private Integer id;
	
	@Basic(fetch = FetchType.LAZY)
	@NotNull
	@Column(name="ob_binario")
	private byte[] binario;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public byte[] getBinario() {
		return binario;
	}

	public void setBinario(byte[] binario) {
		this.binario = binario;
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
		if (!(obj instanceof Binario))
			return false;
		Binario other = (Binario) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
}
