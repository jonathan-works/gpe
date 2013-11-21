package br.com.infox.epp.estatistica.entity;

import java.io.Serializable;
import java.text.MessageFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.infox.core.constants.FloatFormatConstants;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.ibpm.type.PrazoEnum;

@Entity
@Table(name=TempoMedioTarefa.TABLE_NAME, schema="public")
public class TempoMedioTarefa implements Serializable {
	
	private static final int MINUTES_OF_HOUR = 60;
    private static final int MINUTES_OF_DAY = 1440;
    private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "vs_tempo_medio_tarefa";

	private Integer idTarefa;
	private Tarefa tarefa;
	private String nomeTarefa;
	private TempoMedioProcesso tempoMedioProcesso;
	private Float tempoMedio;
	private PrazoEnum tipoPrazo;
	private Integer prazo;

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
	
	@Column(name="nr_tempo_medio", insertable=false, updatable=false)
	public Float getTempoMedio() {
		return this.tempoMedio;
	}
	public void setTempoMedio(Float tempoMedio) {
		this.tempoMedio = tempoMedio;
	}
	
	@Transient
	public String getTempoMedioFormatado() {
	    float resultTempo = tempoMedio;
	    if(PrazoEnum.D.equals(tipoPrazo)) {
	        resultTempo = resultTempo / MINUTES_OF_DAY;
	    } else if (PrazoEnum.H.equals(tipoPrazo)) {
	        resultTempo = resultTempo / MINUTES_OF_HOUR;
	    }
	    return String.format(FloatFormatConstants._2F_S, resultTempo, tipoPrazo == null ? "":tipoPrazo.getLabel());
	}
	
	@Column(name="tp_prazo")
	@Enumerated(EnumType.STRING)
	public PrazoEnum getTipoPrazo() {
		return tipoPrazo;
	}
	public void setTipoPrazo(PrazoEnum tipoPrazo) {
		this.tipoPrazo = tipoPrazo;
	}
	
	@Column(name="nr_prazo")
	public Integer getPrazo() {
		return prazo;
	}
	public void setPrazo(Integer prazo) {
		this.prazo = prazo;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("{0} - {1}d", this.tempoMedioProcesso, this.tempoMedio);
	}
}
