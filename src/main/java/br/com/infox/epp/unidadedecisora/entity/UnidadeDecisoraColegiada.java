package br.com.infox.epp.unidadedecisora.entity;

import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.SEARCH_UDC_BY_USUARIO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.SEARCH_UDC_BY_USUARIO_QUERY;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.SEARCH_EXISTE_UDC_BY_LOCALIZACAO;
import static br.com.infox.epp.unidadedecisora.queries.UnidadeDecisoraColegiadaQuery.SEARCH_EXISTE_UDC_BY_LOCALIZACAO_QUERY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.Localizacao;

@Entity
@Table(name = UnidadeDecisoraColegiada.TABLE_NAME)
@NamedQueries(value={
		@NamedQuery(name=SEARCH_UDC_BY_USUARIO, query=SEARCH_UDC_BY_USUARIO_QUERY),
		@NamedQuery(name=SEARCH_EXISTE_UDC_BY_LOCALIZACAO, query=SEARCH_EXISTE_UDC_BY_LOCALIZACAO_QUERY)
})
public class UnidadeDecisoraColegiada implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_uni_decisora_colegiada";
	
	@Id
	@SequenceGenerator(allocationSize=1, initialValue=1, name="UnidadeDecisoraColegiadaGenerator", sequenceName="sq_uni_decisora_colegiada")
	@GeneratedValue(generator = "UnidadeDecisoraColegiadaGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name="id_uni_decisora_colegiada", unique = true, nullable = false)
	private Integer idUnidadeDecisoraColegiada;
	
	@NotNull
	@Column(name = "ds_uni_decisora_colegiada", nullable = false, unique=true)
	private String nome;
	
	@NotNull
	@Column(name = "in_ativo", nullable = false)
	private Boolean ativo;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH, mappedBy="unidadeDecisoraColegiada")
	@OrderBy("unidadeDecisoraMonocratica ASC")
	private List<UnidadeDecisoraColegiadaMonocratica> unidadeDecisoraColegiadaMonocraticaList = new ArrayList<>();
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_localizacao", nullable=false)
	private Localizacao localizacao;
	
	public Integer getIdUnidadeDecisoraColegiada() {
		return idUnidadeDecisoraColegiada;
	}

	public void setIdUnidadeDecisoraColegiada(Integer idUnidadeDecisoraColegiada) {
		this.idUnidadeDecisoraColegiada = idUnidadeDecisoraColegiada;
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
	
	public List<UnidadeDecisoraColegiadaMonocratica> getUnidadeDecisoraColegiadaMonocraticaList() {
		return unidadeDecisoraColegiadaMonocraticaList;
	}

	public void setUnidadeDecisoraColegiadaMonocraticaList(
			List<UnidadeDecisoraColegiadaMonocratica> unidadeDecisoraColegiadaMonocraticaList) {
		this.unidadeDecisoraColegiadaMonocraticaList = unidadeDecisoraColegiadaMonocraticaList;
	}
	
	public Localizacao getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}

	@Transient
	public List<UnidadeDecisoraMonocratica> getUnidadeDecisoraMonocraticaList(){
		List<UnidadeDecisoraMonocratica> unidadeDecisoraMonocraticaList = new ArrayList<>();
		for (UnidadeDecisoraColegiadaMonocratica colegiadaMonocratica : getUnidadeDecisoraColegiadaMonocraticaList()){
			unidadeDecisoraMonocraticaList.add(colegiadaMonocratica.getUnidadeDecisoraMonocratica());
		}
		return unidadeDecisoraMonocraticaList;
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
				+ ((idUnidadeDecisoraColegiada == null) ? 0
						: idUnidadeDecisoraColegiada.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UnidadeDecisoraColegiada))
			return false;
		UnidadeDecisoraColegiada other = (UnidadeDecisoraColegiada) obj;
		if (idUnidadeDecisoraColegiada == null) {
			if (other.idUnidadeDecisoraColegiada != null)
				return false;
		} else if (!idUnidadeDecisoraColegiada
				.equals(other.idUnidadeDecisoraColegiada))
			return false;
		return true;
	}
	
}
