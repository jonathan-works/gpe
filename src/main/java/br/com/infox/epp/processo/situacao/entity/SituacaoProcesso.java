package br.com.infox.epp.processo.situacao.entity;

import java.io.Serializable;
import java.text.MessageFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import br.com.infox.epp.processo.sigilo.filter.SigiloProcessoFilter;

@Entity
@Table(name = SituacaoProcesso.TABLE_NAME)
@FilterDefs({
    @FilterDef(name = SigiloProcessoFilter.FILTER_SIGILO_PROCESSO, parameters = { 
        @ParamDef(type = SigiloProcessoFilter.TYPE_INT, name = SigiloProcessoFilter.PARAM_ID_USUARIO) }) })
@Filters({
    @Filter(name = SigiloProcessoFilter.FILTER_SIGILO_PROCESSO, condition = SigiloProcessoFilter.CONDITION_FILTER_SIGILO_PROCESSO) 
})
public class SituacaoProcesso implements Serializable {

    public static final String TABLE_NAME = "vs_situacao_processo";
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_situacao_processo", insertable = false, updatable = false)
    private Long id;
    
    @Column(name = "nm_pooled_actor", insertable = false, updatable = false)
    private String pooledActor;
    
    @Column(name = "nm_fluxo", insertable = false, updatable = false)
    private String nomeFluxo;
    
    @Column(name = "nm_tarefa", insertable = false, updatable = false)
    private String nomeTarefa;
    
    @Column(name = "nm_caixa", insertable = false, updatable = false)
    private String nomeCaixa;
    
    @Column(name = "id_fluxo", insertable = false, updatable = false)
    private Integer idFluxo;
    
    @Column(name = "id_tarefa", insertable = false, updatable = false)
    private Integer idTarefa;
    
    @Column(name = "id_caixa", insertable = false, updatable = false)
    private Integer idCaixa;
    
    @Column(name = "id_processo", insertable = false, updatable = false)
    private Integer idProcesso;
    
    @Column(name = "id_process_instance", insertable = false, updatable = false)
    private Long idProcessInstance;
    
    @Column(name = "id_task_instance", insertable = false, updatable = false)
    private Long idTaskInstance;
    
    @Column(name = "id_task", insertable = false, updatable = false)
    private Long idTask;
    
    @Column(name = "nm_actorid", insertable = false, updatable = false)
    private String actorId;

    public Long getIdSituacaoProcesso() {
        return id;
    }

    public void setIdSituacaoProcesso(Long id) {
        this.id = id;
    }

    public String getPooledActor() {
        return pooledActor;
    }

    public void setPooledActor(String pooledActor) {
        this.pooledActor = pooledActor;
    }

    public String getNomeFluxo() {
        return nomeFluxo;
    }

    public void setNomeFluxo(String nomeFluxo) {
        this.nomeFluxo = nomeFluxo;
    }

    public String getNomeTarefa() {
        return nomeTarefa;
    }

    public void setNomeTarefa(String nomeTarefa) {
        this.nomeTarefa = nomeTarefa;
    }

    public String getNomeCaixa() {
        return nomeCaixa;
    }

    public void setNomeCaixa(String nomeCaixa) {
        this.nomeCaixa = nomeCaixa;
    }

    public Integer getIdProcesso() {
        return idProcesso;
    }

    public void setIdProcesso(Integer idProcesso) {
        this.idProcesso = idProcesso;
    }

    public Long getIdProcessInstance() {
        return idProcessInstance;
    }

    public void setIdProcessInstance(Long idProcessInstance) {
        this.idProcessInstance = idProcessInstance;
    }

    public Long getIdTaskInstance() {
        return idTaskInstance;
    }

    public void setIdTaskInstance(Long idTaskInstance) {
        this.idTaskInstance = idTaskInstance;
    }

    public Long getIdTask() {
        return idTask;
    }

    public void setIdTask(Long idTask) {
        this.idTask = idTask;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public void setIdCaixa(Integer idCaixa) {
        this.idCaixa = idCaixa;
    }

    public Integer getIdCaixa() {
        return idCaixa;
    }

    public Integer getIdTarefa() {
        return idTarefa;
    }

    public void setIdTarefa(Integer idTarefa) {
        this.idTarefa = idTarefa;
    }

    public void setIdFluxo(Integer idFluxo) {
        this.idFluxo = idFluxo;
    }

    public Integer getIdFluxo() {
        return idFluxo;
    }
    
    @Override
    public String toString() {
        return MessageFormat.format("{0}:{1}:{2}:{3}", nomeFluxo, nomeTarefa, nomeCaixa, idProcesso);
    }

}
