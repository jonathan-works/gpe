package br.com.infox.epp.tarefa.entity;

import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.DATA_INICIO_PRIMEIRA_TAREFA;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.DATA_INICIO_PRIMEIRA_TAREFA_QUERY;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.FORA_PRAZO_FLUXO;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.FORA_PRAZO_FLUXO_QUERY;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.FORA_PRAZO_TAREFA;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.FORA_PRAZO_TAREFA_QUERY;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.GET_PROCESSO_TAREFA_BY_TASKINSTNACE;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.GET_PROCESSO_TAREFA_BY_TASKINSTNACE_QUERY;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.PROCESSO_TAREFA_BY_ID_PROCESSO_AND_ID_TAREFA;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.PROCESSO_TAREFA_BY_ID_PROCESSO_AND_ID_TAREFA_QUERY;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.TAREFA_COM_EXPIRACAO;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.TAREFA_COM_EXPIRACAO_QUERY;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.TAREFA_ENDED;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.TAREFA_ENDED_QUERY;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.TAREFA_NOT_ENDED_BY_TIPO_PRAZO;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.TAREFA_NOT_ENDED_BY_TIPO_PRAZO_QUERY;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.TAREFA_PROXIMA_LIMITE;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.TAREFA_PROXIMA_LIMITE_QUERY;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.PROCESSO_TAREFA_ABERTO;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.PROCESSO_TAREFA_ABERTO_QUERY;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.tarefa.query.ProcessoTarefaQuery;
import br.com.infox.epp.tarefa.type.PrazoEnum;

@Entity
@Table(name = ProcessoTarefa.TABLE_NAME)
@NamedQueries({
    @NamedQuery(name = GET_PROCESSO_TAREFA_BY_TASKINSTNACE, query = GET_PROCESSO_TAREFA_BY_TASKINSTNACE_QUERY),
    @NamedQuery(name = TAREFA_NOT_ENDED_BY_TIPO_PRAZO, query = TAREFA_NOT_ENDED_BY_TIPO_PRAZO_QUERY),
    @NamedQuery(name = PROCESSO_TAREFA_BY_ID_PROCESSO_AND_ID_TAREFA, query = PROCESSO_TAREFA_BY_ID_PROCESSO_AND_ID_TAREFA_QUERY),
    @NamedQuery(name = FORA_PRAZO_FLUXO, query = FORA_PRAZO_FLUXO_QUERY),
    @NamedQuery(name = FORA_PRAZO_TAREFA, query = FORA_PRAZO_TAREFA_QUERY),
    @NamedQuery(name = TAREFA_PROXIMA_LIMITE, query = TAREFA_PROXIMA_LIMITE_QUERY),
    @NamedQuery(name = TAREFA_ENDED, query = TAREFA_ENDED_QUERY),
    @NamedQuery(name = DATA_INICIO_PRIMEIRA_TAREFA, query = DATA_INICIO_PRIMEIRA_TAREFA_QUERY),
    @NamedQuery(name = TAREFA_COM_EXPIRACAO, query = TAREFA_COM_EXPIRACAO_QUERY),
    @NamedQuery(name = PROCESSO_TAREFA_ABERTO, query = PROCESSO_TAREFA_ABERTO_QUERY),
    @NamedQuery(name = ProcessoTarefaQuery.PROCESSOS_TAREFA, query = ProcessoTarefaQuery.PROCESSOS_TAREFA_QUERY),
    @NamedQuery(name = ProcessoTarefaQuery.ULTIMO_PROCESSO_TAREFA, query = ProcessoTarefaQuery.ULTIMO_PROCESSO_TAREFA_QUERY),
})
public class ProcessoTarefa implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "tb_processo_tarefa";

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = "ProcessoTarefaGenerator", sequenceName = "sq_processo_tarefa")
    @GeneratedValue(generator = "ProcessoTarefaGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_processo_tarefa", unique = true, nullable = false)
    private Integer idProcessoTarefa;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo", nullable = false)
    private Processo processo;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tarefa", nullable = false)
    private Tarefa tarefa;
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_inicio", nullable = false)
    private Date dataInicio;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_fim")
    private Date dataFim;
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_ultimo_disparo", nullable = false)
    private Date ultimoDisparo;
    
    @NotNull
    @Column(name = "nr_tempo_gasto", nullable = false)
    private Integer tempoGasto;
    
    @NotNull
    @Column(name = "nr_tempo_previsto", nullable = false)
    private Integer tempoPrevisto;
    
    @Column(name = "id_task_instance", nullable = false)
    private Long taskInstance;
    
    @Transient
    public String getTempoGastoFormatado() {
        return PrazoEnum.formatTempo(getTempoGasto(), getTarefa().getTipoPrazo());
    }

	public Integer getIdProcessoTarefa() {
		return idProcessoTarefa;
	}

	public void setIdProcessoTarefa(Integer idProcessoTarefa) {
		this.idProcessoTarefa = idProcessoTarefa;
	}

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public Tarefa getTarefa() {
		return tarefa;
	}

	public void setTarefa(Tarefa tarefa) {
		this.tarefa = tarefa;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Date getUltimoDisparo() {
		return ultimoDisparo;
	}

	public void setUltimoDisparo(Date ultimoDisparo) {
		this.ultimoDisparo = ultimoDisparo;
	}

	public Float getPorcentagem() {
		Integer tempoPrevisto = getTempoPrevisto();
		if (tempoPrevisto == 0) {
			return 0f;
		}
		PrazoEnum tipoPrazo = getTarefa().getTipoPrazo();
		if (tipoPrazo == PrazoEnum.H) {
			tempoPrevisto = tempoPrevisto * 60;
		}
		return (getTempoGasto().floatValue()/tempoPrevisto.floatValue()) * 100;
	}

	public Integer getTempoGasto() {
		return tempoGasto;
	}

	public void setTempoGasto(Integer tempoGasto) {
		this.tempoGasto = tempoGasto;
	}

	public Integer getTempoPrevisto() {
		return tempoPrevisto;
	}

	public void setTempoPrevisto(Integer tempoPrevisto) {
		this.tempoPrevisto = tempoPrevisto;
	}

	public Long getTaskInstance() {
		return taskInstance;
	}

	public void setTaskInstance(Long taskInstance) {
		this.taskInstance = taskInstance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((getIdProcessoTarefa() == null) ? 0 : getIdProcessoTarefa().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ProcessoTarefa))
			return false;
		ProcessoTarefa other = (ProcessoTarefa) obj;
		if (getIdProcessoTarefa() == null) {
			if (other.getIdProcessoTarefa() != null)
				return false;
		} else if (!getIdProcessoTarefa().equals(other.getIdProcessoTarefa()))
			return false;
		return true;
	}
	
}
