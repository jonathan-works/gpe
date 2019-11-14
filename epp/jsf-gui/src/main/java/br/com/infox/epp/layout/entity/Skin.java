package br.com.infox.epp.layout.entity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;

@Entity
@Table(name = Skin.TABLE_NAME)
@Cacheable
public class Skin implements Comparable<Skin> {
	public static final String TABLE_NAME = "tb_skin";
	
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "SkinGenerator", sequenceName = "sq_skin")
	@GeneratedValue(generator = "SkinGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_skin", nullable = false, unique = true)
	private Long id;
	
	@NotNull
	@Size(max=LengthConstants.CODIGO_DOCUMENTO)
	@Column(name = "cd_skin", unique = true)
	private String codigo;
	
	@NotNull
	@Size(max=LengthConstants.DESCRICAO_PEQUENA)
	@Column(name = "nm_skin")
	private String nome;
	
	@Column(name = "in_padrao")
	private boolean padrao;
	
	public Long getId() {
		return id;
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

	public boolean isPadrao() {
		return padrao;
	}

	public void setPadrao(boolean padrao) {
		this.padrao = padrao;
	}
	
	@Override
	public String toString() {
		return nome;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		Skin other = (Skin) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

	@Override
	public int compareTo(Skin o) {
		if(this == o) {
			return 0;
		}
		if(o==null) {
			return +1;
		}
		return this.getCodigo().compareTo(o.getCodigo());
	}
	
}
