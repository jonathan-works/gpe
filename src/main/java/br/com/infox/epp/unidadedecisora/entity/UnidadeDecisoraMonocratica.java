package br.com.infox.epp.unidadedecisora.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.Localizacao;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraMonocraticaQuery.*;

@Entity
@Table(name = UnidadeDecisoraMonocratica.TABLE_NAME)
@NamedQueries(value={ @NamedQuery(name=SEARCH_BY_UNIDADE_DECISORA_COLEGIADA, query=SEARCH_BY_UNIDADE_DECISORA_COLEGIADA_QUERY) })
public class UnidadeDecisoraMonocratica implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_uni_decisora_monocratica";
	
	@Id
	@SequenceGenerator(allocationSize=1, initialValue=1, name = "UnidadeDecisoraMonocraticaGenerator", sequenceName="sq_uni_decisora_monocratica")
	@GeneratedValue(generator="UnidadeDecisoraMonocraticaGenerator", strategy=GenerationType.SEQUENCE)
	@Column(name = "id_uni_decisora_monocratica", unique=true, nullable=false)
	private Integer idUnidadeDecisoraMonocratica;
	
	@NotNull
	@Column(name="ds_uni_decisora_monocratica", nullable=false, unique=true)
	private String nome;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_localizacao", nullable=false)
	private Localizacao localizacao;
	
	@NotNull
	@Column(name="in_ativo", nullable = false)
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
