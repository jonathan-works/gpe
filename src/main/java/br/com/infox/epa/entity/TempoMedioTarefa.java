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

import br.com.infox.ibpm.entity.Tarefa;

@Entity
@Table(name=TempoMedioTarefa.TABLE_NAME, schema="public")
public class TempoMedioTarefa implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "vs_tempo_medio_tarefa";

	private Integer idTarefa;
	private Tarefa tarefa;
	private String nomeTarefa;
	private TempoMedioProcesso tempoMedioProcesso;
	private String tempoMedio;

	@Id
	@Column(name = "id_tarefa", nullable=false, insertable=false, updatable=false)
	public Integer getIdTarefa() {
		return idTarefa;
	}
	public void setIdTarefa(Integer idTarefa) {
		this.idTarefa = idTarefa;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_tarefa", insertable=false, updatable=false)
	public Tarefa getTarefa() {
		return tarefa;
	}
	public void setTarefa(Tarefa tarefa) {
		this.tarefa = tarefa;
		this.nomeTarefa = tarefa.getTarefa();
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_natureza_categoria_fluxo", insertable=false, updatable=false)
	public TempoMedioProcesso getTempoMedioProcesso() {
		return tempoMedioProcesso;
	}
	public void setTempoMedioProcesso(
			TempoMedioProcesso tempoMedioProcesso) {
		this.tempoMedioProcesso = tempoMedioProcesso;
	}
	
	@Transient
	public String getNomeTarefa() {
		return this.nomeTarefa;
	}
	public void setNomeTarefa(String nomeTarefa) {
		this.nomeTarefa = nomeTarefa;
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
		StringBuilder sb = new StringBuilder();
		
		sb.append(tempoMedioProcesso.toString());
		sb.append(" - ");
		sb.append(this.tempoMedio.split(":")[0]);
		sb.append("d");
		
		return sb.toString();
	}
}
