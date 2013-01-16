package br.com.infox.epa.entity;

import java.io.Serializable;
import java.text.MessageFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name=TempoMedioProcesso.TABLE_NAME, schema="public")
public class TempoMedioProcesso implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "vs_tempo_medio_processo";

	private Integer idNaturezaCategoriaFluxo;
	private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
	private Float tempoMedio;
	private Integer prazo;

	@Id
	@Column(name = "id_natureza_categoria_fluxo", nullable=false, insertable=false, updatable=false)
	public Integer getIdNaturezaCategoriaFluxo() {
		return idNaturezaCategoriaFluxo;
	}
	public void setIdNaturezaCategoriaFluxo(Integer idNaturezaCategoriaFluxo) {
		this.idNaturezaCategoriaFluxo = idNaturezaCategoriaFluxo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_natureza_categoria_fluxo", insertable=false, updatable=false)
	public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() {
		return naturezaCategoriaFluxo;
	}
	public void setNaturezaCategoriaFluxo(
			NaturezaCategoriaFluxo naturezaCategoriaFluxo) {
		this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
	}
	
	@Column(name="nr_tempo_medio", insertable=false, updatable=false)
	public Float getTempoMedio() {
		return this.tempoMedio;
	}
	public void setTempoMedio(Float tempoMedio) {
		this.tempoMedio = tempoMedio;
	}
	
	@Column(name="qt_prazo", insertable=false, updatable=false)
	public Integer getPrazo() {
		return this.prazo;
	}
	public void setPrazo(Integer prazo) {
		this.prazo = prazo;
	}
	
	@Transient
	public String getTempoMedioFormatado() {
		return String.format("%.2f", this.tempoMedio);
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("{0}", this.naturezaCategoriaFluxo.toString());
	}
}
