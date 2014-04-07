package br.com.infox.epp.tarefa.entity;

import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.FORA_PRAZO_FLUXO;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.FORA_PRAZO_FLUXO_QUERY;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.FORA_PRAZO_TAREFA;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.FORA_PRAZO_TAREFA_QUERY;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.GET_PROCESSO_EPA_TAREFA_BY_TASKINSTNACE;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.GET_PROCESSO_EPA_TAREFA_BY_TASKINSTNACE_QUERY;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.PROCESSO_EPA_TAREFA_BY_ID_PROCESSO_AND_ID_TAREFA;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.PROCESSO_EPA_TAREFA_BY_ID_PROCESSO_AND_ID_TAREFA_QUERY;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.TAREFA_ENDED;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.TAREFA_ENDED_QUERY;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.TAREFA_NOT_ENDED_BY_TIPO_PRAZO;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.TAREFA_NOT_ENDED_BY_TIPO_PRAZO_QUERY;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.TAREFA_PROXIMA_LIMITE;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.TAREFA_PROXIMA_LIMITE_QUERY;

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

import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.tarefa.type.PrazoEnum;

@Entity
@Table(name = ProcessoEpaTarefa.TABLE_NAME)
@NamedQueries({
    @NamedQuery(name = GET_PROCESSO_EPA_TAREFA_BY_TASKINSTNACE, query = GET_PROCESSO_EPA_TAREFA_BY_TASKINSTNACE_QUERY),
    @NamedQuery(name = TAREFA_NOT_ENDED_BY_TIPO_PRAZO, query = TAREFA_NOT_ENDED_BY_TIPO_PRAZO_QUERY),
    @NamedQuery(name = PROCESSO_EPA_TAREFA_BY_ID_PROCESSO_AND_ID_TAREFA, query = PROCESSO_EPA_TAREFA_BY_ID_PROCESSO_AND_ID_TAREFA_QUERY),
    @NamedQuery(name = FORA_PRAZO_FLUXO, query = FORA_PRAZO_FLUXO_QUERY),
    @NamedQuery(name = FORA_PRAZO_TAREFA, query = FORA_PRAZO_TAREFA_QUERY),
    @NamedQuery(name = TAREFA_PROXIMA_LIMITE, query = TAREFA_PROXIMA_LIMITE_QUERY),
    @NamedQuery(name = TAREFA_ENDED, query = TAREFA_ENDED_QUERY) })
public class ProcessoEpaTarefa implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "tb_processo_epa_tarefa";

    private int idProcessoTarefa;
    private ProcessoEpa processoEpa;
    private Tarefa tarefa;
    private Date dataInicio;
    private Date dataFim;
    private Date ultimoDisparo;
    private Integer porcentagem;
    private Integer tempoGasto;
    private Integer tempoPrevisto;
    private Long taskInstance;

    @SequenceGenerator(name = "generator", sequenceName = "sq_tb_processo_epa_tarefa")
    @Id
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_processo_epa_tarefa", unique = true, nullable = false)
    public int getIdProcessoTarefa() {
        return idProcessoTarefa;
    }

    public void setIdProcessoTarefa(int idProcessoTarefa) {
        this.idProcessoTarefa = idProcessoTarefa;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo", nullable = false)
    @NotNull
    public ProcessoEpa getProcessoEpa() {
        return processoEpa;
    }

    public void setProcessoEpa(ProcessoEpa processo) {
        this.processoEpa = processo;
    }

    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tarefa", nullable = false)
    @NotNull
    public Tarefa getTarefa() {
        return tarefa;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    @Column(name = "dt_inicio", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    @Column(name = "dt_fim")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDataFim() {
        return dataFim;
    }

    public void setTaskInstance(Long taskInstance) {
        this.taskInstance = taskInstance;
    }

    @Column(name = "id_task_instance", nullable = false)
    public Long getTaskInstance() {
        return this.taskInstance;
    }

    public void setUltimoDisparo(Date ultimoDisparo) {
        this.ultimoDisparo = ultimoDisparo;
    }

    @Column(name = "dt_ultimo_disparo", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    public Date getUltimoDisparo() {
        return ultimoDisparo;
    }

    public void setTempoGasto(Integer tempoGasto) {
        this.tempoGasto = tempoGasto;
    }

    @Column(name = "nr_tempo_gasto", nullable = false)
    @NotNull
    public Integer getTempoGasto() {
        return tempoGasto;
    }

    @Transient
    public String getTempoGastoFormatado() {
        return PrazoEnum.formatTempo(tempoGasto, tarefa.getTipoPrazo());
    }

    public void setTempoPrevisto(Integer tempoPrevisto) {
        this.tempoPrevisto = tempoPrevisto;
    }

    @Column(name = "nr_tempo_previsto", nullable = false)
    @NotNull
    public Integer getTempoPrevisto() {
        return tempoPrevisto;
    }

    public void setPorcentagem(Integer porcentagem) {
        this.porcentagem = porcentagem;
    }

    @Column(name = "nr_porcentagem")
    public Integer getPorcentagem() {
        return porcentagem;
    }

}
