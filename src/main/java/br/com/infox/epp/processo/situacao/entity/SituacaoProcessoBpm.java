package br.com.infox.epp.processo.situacao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = SituacaoProcessoBpm.TABLE_NAME, schema="public")

public class SituacaoProcessoBpm implements java.io.Serializable {

	public static final String TABLE_NAME = "vs_situacao_processo_bpm";
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String pooledActor;
	private String nomeFluxo;
	private String nomeTarefa;
	private Integer idTarefa;
	private Integer idProcesso;
	private Long idProcessInstance;
	private Long idTaskInstance;
	private Long idTask;
	private String actorId;
	
	public SituacaoProcessoBpm() {
	}

	@Id
	@Column(name="id_situacao_processo", insertable=false, updatable=false)
	public Long getIdSituacaoProcesso() {
		return id;
	}

	public void setIdSituacaoProcesso(Long id) {
		this.id= id;
	}
	
	@Column(name="nm_pooled_actor", insertable=false, updatable=false)
	public String getPooledActor() {
		return pooledActor;
	}

	public void setPooledActor(String pooledActor) {
		this.pooledActor = pooledActor;
	}

	@Column(name="nm_fluxo", insertable=false, updatable=false)
	public String getNomeFluxo() {
		return nomeFluxo;
	}

	public void setNomeFluxo(String nomeFluxo) {
		this.nomeFluxo = nomeFluxo;
	}

	@Column(name="nm_tarefa", insertable=false, updatable=false)
	public String getNomeTarefa() {
		return nomeTarefa;
	}

	public void setNomeTarefa(String nomeTarefa) {
		this.nomeTarefa = nomeTarefa;
	}

	@Column(name="id_processo", insertable=false, updatable=false)
	public Integer getIdProcesso() {
		return idProcesso;
	}

	public void setIdProcesso(Integer idProcesso) {
		this.idProcesso = idProcesso;
	}

	@Column(name="id_process_instance", insertable=false, updatable=false)
	public Long getIdProcessInstance() {
		return idProcessInstance;
	}

	public void setIdProcessInstance(Long idProcessInstance) {
		this.idProcessInstance = idProcessInstance;
	}

	@Column(name="id_task_instance", insertable=false, updatable=false)
	public Long getIdTaskInstance() {
		return idTaskInstance;
	}

	public void setIdTaskInstance(Long idTaskInstance) {
		this.idTaskInstance = idTaskInstance;
	}

	@Column(name="id_task", insertable=false, updatable=false)
	public Long getIdTask() {
		return idTask;
	}

	public void setIdTask(Long idTask) {
		this.idTask = idTask;
	}

	@Column(name="nm_actorid", insertable=false, updatable=false)
	public String getActorId() {
		return actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	@Column(name="id_tarefa", insertable=false, updatable=false)
	public Integer getIdTarefa() {
		return idTarefa;
	}

	public void setIdTarefa(Integer idTarefa) {
		this.idTarefa = idTarefa;
	}	

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getIdSituacaoProcesso() == null) {
			return false;
		}
		if (!(obj instanceof SituacaoProcessoBpm)) {
			return false;
		}
		SituacaoProcessoBpm other = (SituacaoProcessoBpm) obj;
		return getIdSituacaoProcesso().equals(other.getIdSituacaoProcesso()); 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getIdSituacaoProcesso() == null) ? 0 : getIdSituacaoProcesso().hashCode());
		return result;
	}
}