package br.com.infox.epp.estatistica.entity;

import static br.com.infox.core.persistence.ORConstants.*;
import static br.com.infox.epp.estatistica.query.TempoMedioTarefaQuery.*;
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
import br.com.infox.epp.tarefa.type.PrazoEnum;

@Entity
@Table(name=VIEW_TEMPO_MEDIO_TAREFA, schema=PUBLIC)
public class TempoMedioTarefa implements Serializable {
	
	private static final int MINUTES_OF_HOUR = 60;
    private static final int MINUTES_OF_DAY = 1440;
    private static final long serialVersionUID = 1L;

	private Integer idTarefa;
	private Tarefa tarefa;
	private String nomeTarefa;
	private TempoMedioProcesso tempoMedioProcesso;
	private Float tempoMedio;
	private PrazoEnum tipoPrazo;
	private Integer prazo;

	@Id
	@Column(name = ID_TAREFA, nullable=false, insertable=false, updatable=false)
	public Integer getIdTarefa() {
		return idTarefa;
	}
	public void setIdTarefa(Integer idTarefa) {
		this.idTarefa = idTarefa;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name=ID_TAREFA, insertable=false, updatable=false)
	public Tarefa getTarefa() {
		return tarefa;
	}
	public void setTarefa(Tarefa tarefa) {
		this.tarefa = tarefa;
		this.nomeTarefa = tarefa.getTarefa();
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name=ID_NATUREZA_CATEGORIA_FLUXO, insertable=false, updatable=false)
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
	
	@Column(name=TEMPO_MEDIO, insertable=false, updatable=false)
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
	    return String.format(FloatFormatConstants.F2_S, resultTempo, tipoPrazo == null ? "":tipoPrazo.getLabel());
	}
	
	@Column(name=TIPO_PRAZO)
	@Enumerated(EnumType.STRING)
	public PrazoEnum getTipoPrazo() {
		return tipoPrazo;
	}
	public void setTipoPrazo(PrazoEnum tipoPrazo) {
		this.tipoPrazo = tipoPrazo;
	}
	
	@Column(name=PRAZO)
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
