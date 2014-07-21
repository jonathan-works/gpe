package br.com.infox.epp.unidadedecisora.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = UniDecisoraColegiadaMono.TABLE_NAME, 
	   uniqueConstraints = @UniqueConstraint(columnNames={"id_uni_decisora_monocratica", "id_uni_decisora_colegiada"}))
public class UniDecisoraColegiadaMono implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_uni_decisora_colegiada_mono";
	
	@Id
	@SequenceGenerator(allocationSize=1, initialValue=1, name="UniDecisoraColegiadaMonoGenerator", sequenceName="sq_uni_decisora_colegiada_mono")
	@GeneratedValue(generator="UniDecisoraColegiadaMonoGenerator")
	@Column(name="id_uni_decisora_colegiada_mono", unique = true, nullable = false)
	private Integer idUniDecisoraColegiadaMono;
	
	@NotNull
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH)
	@JoinColumn(name="id_uni_decisora_monocratica", updatable=false, nullable=false)
	private UnidadeDecisoraMonocratica unidadeDecisoraMonocratica;
	
	@NotNull
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH)
	@JoinColumn(name="id_uni_decisora_colegiada", updatable=false, nullable=false)
	private UnidadeDecisoraColegiada unidadeDecisoraColegiada;
	
	@Column(name="in_presidente", nullable=false)
	private Boolean presidente;
	
	@PrePersist
	private void prePersist(){
		if (presidente == null){
			presidente = Boolean.FALSE;
		}
	}

	public Integer getIdUniDecisoraColegiadaMono() {
		return idUniDecisoraColegiadaMono;
	}

	public void setIdUniDecisoraColegiadaMono(Integer idUniDecisoraColegiadaMono) {
		this.idUniDecisoraColegiadaMono = idUniDecisoraColegiadaMono;
	}

	public UnidadeDecisoraMonocratica getUnidadeDecisoraMonocratica() {
		return unidadeDecisoraMonocratica;
	}

	public void setUnidadeDecisoraMonocratica(UnidadeDecisoraMonocratica unidadeDecisoraMonocratica) {
		this.unidadeDecisoraMonocratica = unidadeDecisoraMonocratica;
	}

	public UnidadeDecisoraColegiada getUnidadeDecisoraColegiada() {
		return unidadeDecisoraColegiada;
	}

	public void setUnidadeDecisoraColegiada(UnidadeDecisoraColegiada unidadeDecisoraColegiada) {
		this.unidadeDecisoraColegiada = unidadeDecisoraColegiada;
	}

	public Boolean getPresidente() {
		return presidente;
	}

	public void setPresidente(Boolean presidente) {
		this.presidente = presidente;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((idUniDecisoraColegiadaMono == null) ? 0
						: idUniDecisoraColegiadaMono.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UniDecisoraColegiadaMono))
			return false;
		UniDecisoraColegiadaMono other = (UniDecisoraColegiadaMono) obj;
		if (idUniDecisoraColegiadaMono == null) {
			if (other.idUniDecisoraColegiadaMono != null)
				return false;
		} else if (!idUniDecisoraColegiadaMono
				.equals(other.idUniDecisoraColegiadaMono))
			return false;
		return true;
	}
	
}
