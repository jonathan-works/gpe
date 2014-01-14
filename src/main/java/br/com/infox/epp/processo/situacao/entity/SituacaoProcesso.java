/* $Id: SituacaoProcesso.java 704 2010-08-12 23:21:10Z jplacerda $ */

package br.com.infox.epp.processo.situacao.entity;

import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.*;

import java.text.MessageFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import br.com.infox.epp.processo.situacao.filter.SituacaoProcessoFilter;

@Entity
@Table(name = SituacaoProcesso.TABLE_NAME, schema="public")
@NamedQueries({
    @NamedQuery(name=TAREFAS_TREE_ROOTS, query=TAREFAS_TREE_QUERY_ROOTS),
    @NamedQuery(name=TAREFAS_TREE_CHILDREN, query=TAREFAS_TREE_QUERY_CHILDREN),
    @NamedQuery(name=TAREFAS_TREE_CAIXAS, query=TAREFAS_TREE_QUERY_CAIXAS),
    @NamedQuery(name=COUNT_TAREFAS_ATIVAS_BY_TASK_ID, query=COUNT_TAREFAS_ATIVAS_BY_TASK_ID_QUERY)
})
@FilterDefs({ 
    @FilterDef(name = SituacaoProcessoFilter.FILTER_PAPEL_LOCALIZACAO, parameters = {
    @ParamDef(type = SituacaoProcessoFilter.TYPE_INT, name = SituacaoProcessoFilter.FILTER_PARAM_ID_LOCALIZACAO),
    @ParamDef(type = SituacaoProcessoFilter.TYPE_INT, name = SituacaoProcessoFilter.FILTER_PARAM_ID_PAPEL) }) })
@Filters({ 
    @Filter(name = SituacaoProcessoFilter.FILTER_PAPEL_LOCALIZACAO, condition = SituacaoProcessoFilter.CONDITION_PAPEL_LOCALIZACAO) 
})
public class SituacaoProcesso implements java.io.Serializable {

	public static final String TABLE_NAME = "vs_situacao_processo";
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String pooledActor;
	private String nomeFluxo;
	private String nomeTarefa;
	private String nomeCaixa;
	private Integer idFluxo;
	private Integer idTarefa;
	private Integer idCaixa;
	private Integer idProcesso;
	private Long idProcessInstance;
	private Long idTaskInstance;
	private Long idTask;
	private String actorId;
	
	public SituacaoProcesso() {
	}

	@Id
	@Column(name="id_situacao_processo", insertable=false, updatable=false)
	public Long getIdSituacaoProcesso() {
		return id;
	}

	public void setIdSituacaoProcesso(Long id) {
		this.id= id;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("{0}:{1}:{2}:{3}", nomeFluxo, nomeTarefa, 
				nomeCaixa, idProcesso);
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

	@Column(name="nm_caixa", insertable=false, updatable=false)
	public String getNomeCaixa() {
		return nomeCaixa;
	}

	public void setNomeCaixa(String nomeCaixa) {
		this.nomeCaixa = nomeCaixa;
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

	public void setIdCaixa(Integer idCaixa) {
		this.idCaixa = idCaixa;
	}

	@Column(name="id_caixa", insertable=false, updatable=false)
	public Integer getIdCaixa() {
		return idCaixa;
	}
	
	@Column(name="id_tarefa", insertable=false, updatable=false)
	public Integer getIdTarefa() {
		return idTarefa;
	}

	public void setIdTarefa(Integer idTarefa) {
		this.idTarefa = idTarefa;
	}

	public void setIdFluxo(Integer idFluxo) {
		this.idFluxo = idFluxo;
	}

	@Column(name="id_fluxo", insertable=false, updatable=false)
	public Integer getIdFluxo() {
		return idFluxo;
	}	
	
}
