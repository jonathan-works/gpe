package br.com.infox.epp.processo.entity;

import static br.com.infox.core.constants.LengthConstants.NUMERACAO_PROCESSO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.processo.query.ProcessoQuery.ANULA_ACTOR_ID;
import static br.com.infox.epp.processo.query.ProcessoQuery.ANULA_ACTOR_ID_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.ANULA_TODOS_OS_ACTOR_IDS;
import static br.com.infox.epp.processo.query.ProcessoQuery.ANULA_TODOS_OS_ACTOR_IDS_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.APAGA_ACTOR_ID_DO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.APAGA_ACTOR_ID_DO_PROCESSO_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.ATUALIZAR_PROCESSOS;
import static br.com.infox.epp.processo.query.ProcessoQuery.ATUALIZAR_PROCESSOS_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.DATA_FIM;
import static br.com.infox.epp.processo.query.ProcessoQuery.DATA_INICIO;
import static br.com.infox.epp.processo.query.ProcessoQuery.DURACAO;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.ID_CAIXA;
import static br.com.infox.epp.processo.query.ProcessoQuery.ID_JBPM;
import static br.com.infox.epp.processo.query.ProcessoQuery.ID_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.ID_USUARIO_CADASTRO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.MOVER_PROCESSOS_PARA_CAIXA;
import static br.com.infox.epp.processo.query.ProcessoQuery.MOVER_PROCESSOS_PARA_CAIXA_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.MOVER_PROCESSO_PARA_CAIXA;
import static br.com.infox.epp.processo.query.ProcessoQuery.MOVER_PROCESSO_PARA_CAIXA_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.NOME_ACTOR_ID;
import static br.com.infox.epp.processo.query.ProcessoQuery.NUMERO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.NUMERO_PROCESSO_BY_ID_JBPM;
import static br.com.infox.epp.processo.query.ProcessoQuery.NUMERO_PROCESSO_BY_ID_JBPM_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSO_ATTRIBUTE;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSO_BY_NUMERO;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSO_BY_NUMERO_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.REMOVER_PROCESSO_JBMP;
import static br.com.infox.epp.processo.query.ProcessoQuery.REMOVER_PROCESSO_JBMP_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.REMOVE_PROCESSO_DA_CAIXA_ATUAL;
import static br.com.infox.epp.processo.query.ProcessoQuery.REMOVE_PROCESSO_DA_CAIXA_ATUAL_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.SEQUENCE_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.TABLE_PROCESSO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.painel.caixa.Caixa;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;

@Entity
@Table(name = TABLE_PROCESSO)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tp_processo", length = 2)
@NamedNativeQueries(value = {
    @NamedNativeQuery(name = APAGA_ACTOR_ID_DO_PROCESSO, query = APAGA_ACTOR_ID_DO_PROCESSO_QUERY),
    @NamedNativeQuery(name = REMOVE_PROCESSO_DA_CAIXA_ATUAL, query = REMOVE_PROCESSO_DA_CAIXA_ATUAL_QUERY),
    @NamedNativeQuery(name = ATUALIZAR_PROCESSOS, query = ATUALIZAR_PROCESSOS_QUERY),
    @NamedNativeQuery(name = ANULA_TODOS_OS_ACTOR_IDS, query = ANULA_TODOS_OS_ACTOR_IDS_QUERY),
    @NamedNativeQuery(name = MOVER_PROCESSO_PARA_CAIXA, query = MOVER_PROCESSO_PARA_CAIXA_QUERY),
    @NamedNativeQuery(name = ANULA_ACTOR_ID, query = ANULA_ACTOR_ID_QUERY),
    @NamedNativeQuery(name = REMOVER_PROCESSO_JBMP, query = REMOVER_PROCESSO_JBMP_QUERY),
    @NamedNativeQuery(name = GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST, query = GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST_QUERY)
})
@NamedQueries(value = {
    @NamedQuery(name = LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID, query = LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID_QUERY),
    @NamedQuery(name = MOVER_PROCESSOS_PARA_CAIXA, query = MOVER_PROCESSOS_PARA_CAIXA_QUERY),
    @NamedQuery(name = PROCESSO_BY_NUMERO, query = PROCESSO_BY_NUMERO_QUERY),
    @NamedQuery(name = NUMERO_PROCESSO_BY_ID_JBPM, query = NUMERO_PROCESSO_BY_ID_JBPM_QUERY)
})
public abstract class Processo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_PROCESSO)
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_PROCESSO, unique = true, nullable = false)
    private Integer idProcesso;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_USUARIO_CADASTRO_PROCESSO)
    private UsuarioLogin usuarioCadastroProcesso;
    
    @NotNull
    @Size(max = NUMERACAO_PROCESSO)
    @Column(name = NUMERO_PROCESSO, nullable = false, length = NUMERACAO_PROCESSO)
    private String numeroProcesso;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tp_processo", insertable = false, updatable = false, nullable = false, length = 2)
    private TipoProcesso tipoProcesso;
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = DATA_INICIO, nullable = false)
    private Date dataInicio;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = DATA_FIM)
    private Date dataFim;
    
    @Column(name = DURACAO)
    private Long duracao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_CAIXA)
    private Caixa caixa;

    @Column(name = ID_JBPM)
    private Long idJbpm;

    @Column(name = NOME_ACTOR_ID)
    private String actorId;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_status_processo", nullable = true)
    private StatusProcesso statusProcesso;
    
    @OneToMany(mappedBy = "processo", fetch = FetchType.LAZY)
    private List<ProcessoTarefa> processoTarefaList = new ArrayList<ProcessoTarefa>(0);
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = PROCESSO_ATTRIBUTE)
    @OrderBy("dataInclusao DESC")
    private List<Documento> documentoList = new ArrayList<Documento>(0);
    
    @OneToMany(mappedBy = "processo", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE })
    @OrderBy(value = "ds_caminho_absoluto")
    private List<ParticipanteProcesso> participantes = new ArrayList<>(0);

    @Transient
    public ProcessoEpa getProcessoEpa(){
		return (ProcessoEpa) this;
    }
    
    @Transient
    public ProcessoDocumento getProcessoDocumento(){
    	return (ProcessoDocumento) this;
    }
    
    public Integer getIdProcesso() {
		return idProcesso;
	}

	public void setIdProcesso(Integer idProcesso) {
		this.idProcesso = idProcesso;
	}

	public UsuarioLogin getUsuarioCadastroProcesso() {
		return usuarioCadastroProcesso;
	}

	public void setUsuarioCadastroProcesso(UsuarioLogin usuarioCadastroProcesso) {
		this.usuarioCadastroProcesso = usuarioCadastroProcesso;
	}

	public String getNumeroProcesso() {
		return numeroProcesso;
	}

	public void setNumeroProcesso(String numeroProcesso) {
		this.numeroProcesso = numeroProcesso;
	}

	public TipoProcesso getTipoProcesso() {
		return tipoProcesso;
	}

	public void setTipoProcesso(TipoProcesso tipoProcesso) {
		this.tipoProcesso = tipoProcesso;
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

	public Caixa getCaixa() {
		return caixa;
	}

	public void setCaixa(Caixa caixa) {
		this.caixa = caixa;
	}

	public Long getIdJbpm() {
		return idJbpm;
	}

	public void setIdJbpm(Long idJbpm) {
		this.idJbpm = idJbpm;
	}

	public String getActorId() {
		return actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public StatusProcesso getStatusProcesso() {
		return statusProcesso;
	}

	public void setStatusProcesso(StatusProcesso statusProcesso) {
		this.statusProcesso = statusProcesso;
	}

	public List<ProcessoTarefa> getProcessoTarefaList() {
		return processoTarefaList;
	}

	public void setProcessoTarefaList(List<ProcessoTarefa> processoTarefaList) {
		this.processoTarefaList = processoTarefaList;
	}

	public Long getDuracao() {
		if (duracao == null && dataFim != null && dataInicio != null) {
			setDuracao(dataFim.getTime() - dataInicio.getTime());
	    }
	    return duracao;
    }

    public void setDuracao(Long duracao) {
        this.duracao = duracao;
    }
    
    public List<Documento> getDocumentoList() {
		return documentoList;
	}
    
    public void setDocumentoList(List<Documento> documentoList) {
		this.documentoList = documentoList;
	}
    
    public List<ParticipanteProcesso> getParticipantes() {
		return participantes;
	}
    
    public void setParticipantes(List<ParticipanteProcesso> participantes) {
		this.participantes = participantes;
	}

	@Override
    public String toString() {
        return numeroProcesso;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getIdProcesso() == null) ? 0 : getIdProcesso().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Processo))
			return false;
		Processo other = (Processo) obj;
		if (getIdProcesso() == null) {
			if (other.getIdProcesso() != null)
				return false;
		} else if (!getIdProcesso().equals(other.getIdProcesso()))
			return false;
		return true;
	}

}
