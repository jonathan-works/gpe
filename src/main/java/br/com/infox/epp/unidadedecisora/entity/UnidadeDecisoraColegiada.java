package br.com.infox.epp.unidadedecisora.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = UnidadeDecisoraColegiada.TABLE_NAME)
public class UnidadeDecisoraColegiada implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_uni_decisora_colegiada";
	
	@Id
	@SequenceGenerator(name="UnidadeDecisoraColegiadaGenerator", sequenceName="sq_tb_unidade_decisora_colegiada")
	@GeneratedValue(generator="UnidadeDecisoraColegiadaGenerator")
	@Column(name="id_uni_decisora_colegiada", nullable = false)
	private Integer idUnidadeDecisoraColegiada;
	
	@NotNull
	@Column(name = "ds_uni_decisora_colegiada", nullable = false)
	private String nome;
	
	@NotNull
	@Column(name = "in_ativo", nullable = false)
	private Boolean ativo;
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinTable(name = "tb_uni_decisora_colegiada_mono", joinColumns = @JoinColumn(name = "id_uni_decisora_colegiada"), inverseJoinColumns = @JoinColumn(name = "id_uni_decisora_monocratica"))
	private List<UnidadeDecisoraMonocratica> unidadeDecisoraMonocraticaList = new ArrayList<>();  

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
	
	public List<UnidadeDecisoraMonocratica> getUnidadeDecisoraMonocraticaList() {
		return unidadeDecisoraMonocraticaList;
	}

	public void setUnidadeDecisoraMonocraticaList(List<UnidadeDecisoraMonocratica> unidadeDecisoraMonocraticaList) {
		this.unidadeDecisoraMonocraticaList = unidadeDecisoraMonocraticaList;
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
