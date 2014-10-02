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
    @NamedQuery(name = TAREFA_ENDED, query = TAREFA_ENDED_QUERY) 
})
public class ProcessoEpaTarefa implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "tb_processo_epa_tarefa";

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = "generator", sequenceName = "sq_tb_processo_epa_tarefa")
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_processo_epa_tarefa", unique = true, nullable = false)
    private int idProcessoTarefa;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo", nullable = false)
    private ProcessoEpa processoEpa;
    
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
    
    @Column(name = "nr_porcentagem")
    private Integer porcentagem;
    
    @NotNull
    @Column(name = "nr_tempo_gasto", nullable = false)
    private Integer tempoGasto;
    
    @NotNull
    @Column(name = "nr_tempo_previsto", nullable = false)
    private Integer tempoPrevisto;
    
    @Column(name = "id_task_instance", nullable = false)
    private Long taskInstance;

    public int getIdProcessoTarefa() {
        return idProcessoTarefa;
    }

    public void setIdProcessoTarefa(int idProcessoTarefa) {
        this.idProcessoTarefa = idProcessoTarefa;
    }

    public ProcessoEpa getProcessoEpa() {
        return processoEpa;
    }

    public void setProcessoEpa(ProcessoEpa processo) {
        this.processoEpa = processo;
    }

    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
    }

    public Tarefa getTarefa() {
        return tarefa;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setTaskInstance(Long taskInstance) {
        this.taskInstance = taskInstance;
    }

    public Long getTaskInstance() {
        return this.taskInstance;
    }

    public void setUltimoDisparo(Date ultimoDisparo) {
        this.ultimoDisparo = ultimoDisparo;
    }

    public Date getUltimoDisparo() {
        return ultimoDisparo;
    }

    public void setTempoGasto(Integer tempoGasto) {
        this.tempoGasto = tempoGasto;
    }

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

    public Integer getTempoPrevisto() {
        return tempoPrevisto;
    }

    public void setPorcentagem(Integer porcentagem) {
        this.porcentagem = porcentagem;
    }

    public Integer getPorcentagem() {
        return porcentagem;
    }

}
