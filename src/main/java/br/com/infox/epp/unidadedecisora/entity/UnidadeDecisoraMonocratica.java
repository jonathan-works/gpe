package br.com.infox.epp.unidadedecisora.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.Localizacao;

@Entity
@Table(name = UnidadeDecisoraMonocratica.TABLE_NAME)
public class UnidadeDecisoraMonocratica implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_uni_decisora_monocratica";
	
	@Id
	@SequenceGenerator(name = "UnidadeDecisoraMonocraticaGenerator", sequenceName="sq_tb_unidade_decisora_monocratica")
	@GeneratedValue(generator="UnidadeDecisoraMonocraticaGenerator")
	@Column(name = "id_uni_decisora_monocratica", nullable = false)
	private Integer idUnidadeDecisoraMonocratica;
	
	@NotNull
	@Column(name = "ds_uni_decisora_monocratica", nullable = false)
	private String nome;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_localizacao", nullable = false)
	private Localizacao localizacao;
	
	@NotNull
	@Column(name = "in_ativo", nullable = false)
	private Boolean ativo;

	public Integer getIdUnidadeDecisoraMonocratica() {
		return idUnidadeDecisoraMonocratica;
	}

	public void setIdUnidadeDecisoraMonocratica(Integer idUnidadeDecisoraMonocratica) {
		this.idUnidadeDecisoraMonocratica = idUnidadeDecisoraMonocratica;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Localizacao getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	@Override
	public String toString() {
		return nome;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((idUnidadeDecisoraMonocratica == null) ? 0
						: idUnidadeDecisoraMonocratica.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UnidadeDecisoraMonocratica))
			return false;
		UnidadeDecisoraMonocratica other = (UnidadeDecisoraMonocratica) obj;
		if (idUnidadeDecisoraMonocratica == null) {
			if (other.idUnidadeDecisoraMonocratica != null)
				return false;
		} else if (!idUnidadeDecisoraMonocratica
				.equals(other.idUnidadeDecisoraMonocratica))
			return false;
		return true;
	}

}
