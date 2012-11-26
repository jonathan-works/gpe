package br.com.infox.epa.entity;

import java.io.Serializable;

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
	private String tempoMedio;

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
	
	@Column(name="ds_tempo_medio", insertable=false, updatable=false)
	public String getTempoMedio() {
		StringBuilder sb = new StringBuilder();
		String[] aux = tempoMedio.split(":");
		
		sb.append(aux[0]);
		sb.append("d ");
		sb.append(aux[1]);
		sb.append("h");
		sb.append(aux[2]);
		sb.append("m");
		
		return sb.toString();
	}
	public void setTempoMedio(String tempoMedio) {
		this.tempoMedio = tempoMedio;
	}
	
	@Transient
	public String getTempoMedioTotalDias() {
		return this.tempoMedio.split(":")[0];
	}
	
	@Transient
	public String getTempoMedioTotalHoras() {
		String[] tempo = tempoMedio.split(":");
		return String.valueOf(Integer.parseInt(tempo[0])*24 + Integer.parseInt(tempo[1]));
	}
	
	@Override
	public String toString() {
		return this.naturezaCategoriaFluxo.toString();
	}
}
