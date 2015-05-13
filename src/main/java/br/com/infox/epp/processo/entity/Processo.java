package br.com.infox.epp.processo.entity;

import static br.com.infox.constants.LengthConstants.NUMERACAO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.ATUALIZAR_PROCESSOS;
import static br.com.infox.epp.processo.query.ProcessoQuery.ATUALIZAR_PROCESSOS_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.COUNT_PARTES_ATIVAS_DO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.COUNT_PARTES_ATIVAS_DO_PROCESSO_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.DATA_FIM; 
import static br.com.infox.epp.processo.query.ProcessoQuery.DATA_INICIO;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_PROCESSO_BY_ID_PROCESSO_AND_ID_USUARIO;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_PROCESSO_BY_ID_PROCESSO_AND_ID_USUARIO_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_PROCESSO_BY_NUMERO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_PROCESSO_BY_NUMERO_PROCESSO_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.ID_CAIXA;
import static br.com.infox.epp.processo.query.ProcessoQuery.ID_JBPM;
import static br.com.infox.epp.processo.query.ProcessoQuery.ID_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.ID_USUARIO_CADASTRO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_ALL_NOT_ENDED;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_ALL_NOT_ENDED_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_NOT_ENDED_BY_FLUXO;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_NOT_ENDED_BY_FLUXO_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_PROCESSOS_COMUNICACAO_SEM_CIENCIA;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_PROCESSOS_COMUNICACAO_SEM_CIENCIA_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_PROCESSOS_COMUNICACAO_SEM_CUMPRIMENTO;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_PROCESSOS_COMUNICACAO_SEM_CUMPRIMENTO_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.NUMERO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.NUMERO_PROCESSO_BY_ID_JBPM;
import static br.com.infox.epp.processo.query.ProcessoQuery.NUMERO_PROCESSO_BY_ID_JBPM_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSOS_BY_ID_CAIXA;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSOS_BY_ID_CAIXA_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSOS_FILHO_BY_TIPO;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSOS_FILHO_BY_TIPO_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSOS_FILHO_NOT_ENDED_BY_TIPO;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSOS_FILHO_NOT_ENDED_BY_TIPO_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSO_ATTRIBUTE;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSO_BY_NUMERO;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSO_BY_NUMERO_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSO_EPA_BY_ID_JBPM;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSO_EPA_BY_ID_JBPM_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.REMOVER_PROCESSO_JBMP;
import static br.com.infox.epp.processo.query.ProcessoQuery.REMOVER_PROCESSO_JBMP_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.TABLE_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.TEMPO_GASTO_PROCESSO_EPP;
import static br.com.infox.epp.processo.query.ProcessoQuery.TEMPO_GASTO_PROCESSO_EPP_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.TEMPO_MEDIO_PROCESSO_BY_FLUXO_AND_SITUACAO;
import static br.com.infox.epp.processo.query.ProcessoQuery.TEMPO_MEDIO_PROCESSO_BY_FLUXO_AND_SITUACAO_QUERY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.persistence.generator.CustomIdGenerator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.painel.caixa.Caixa;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoDefinition;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso;
import br.com.infox.epp.processo.query.ProcessoQuery;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;

@Entity
@Table(name = TABLE_PROCESSO)
@NamedNativeQueries(value = {
    @NamedNativeQuery(name = ATUALIZAR_PROCESSOS, query = ATUALIZAR_PROCESSOS_QUERY),
    @NamedNativeQuery(name = REMOVER_PROCESSO_JBMP, query = REMOVER_PROCESSO_JBMP_QUERY),
    @NamedNativeQuery(name = GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST, query = GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST_QUERY),
    @NamedNativeQuery(name = GET_PROCESSO_BY_ID_PROCESSO_AND_ID_USUARIO, query = GET_PROCESSO_BY_ID_PROCESSO_AND_ID_USUARIO_QUERY,
    				  resultClass = Processo.class)
})
@NamedQueries(value = {
    @NamedQuery(name = PROCESSO_BY_NUMERO, query = PROCESSO_BY_NUMERO_QUERY),
    @NamedQuery(name = NUMERO_PROCESSO_BY_ID_JBPM, query = NUMERO_PROCESSO_BY_ID_JBPM_QUERY),
    @NamedQuery(name = LIST_ALL_NOT_ENDED, query = LIST_ALL_NOT_ENDED_QUERY),
    @NamedQuery(name = PROCESSO_EPA_BY_ID_JBPM, query = PROCESSO_EPA_BY_ID_JBPM_QUERY,
    		    hints = {@QueryHint(name="org.hibernate.cacheable", value="true"),
    					 @QueryHint(name="org.hibernate.cacheRegion", value="br.com.infox.epp.processo.entity.Processo")}),
    @NamedQuery(name = COUNT_PARTES_ATIVAS_DO_PROCESSO, query = COUNT_PARTES_ATIVAS_DO_PROCESSO_QUERY),
    @NamedQuery(name = LIST_NOT_ENDED_BY_FLUXO, query = LIST_NOT_ENDED_BY_FLUXO_QUERY),
    @NamedQuery(name = TEMPO_MEDIO_PROCESSO_BY_FLUXO_AND_SITUACAO, query = TEMPO_MEDIO_PROCESSO_BY_FLUXO_AND_SITUACAO_QUERY),
    @NamedQuery(name = TEMPO_GASTO_PROCESSO_EPP, query = TEMPO_GASTO_PROCESSO_EPP_QUERY),
    @NamedQuery(name = PROCESSOS_FILHO_NOT_ENDED_BY_TIPO, query = PROCESSOS_FILHO_NOT_ENDED_BY_TIPO_QUERY),
    @NamedQuery(name = PROCESSOS_FILHO_BY_TIPO, query = PROCESSOS_FILHO_BY_TIPO_QUERY),
    @NamedQuery(name = GET_PROCESSO_BY_NUMERO_PROCESSO, query = GET_PROCESSO_BY_NUMERO_PROCESSO_QUERY),
    @NamedQuery(name = PROCESSOS_BY_ID_CAIXA, query = PROCESSOS_BY_ID_CAIXA_QUERY),
    @NamedQuery(name = LIST_PROCESSOS_COMUNICACAO_SEM_CIENCIA, query = LIST_PROCESSOS_COMUNICACAO_SEM_CIENCIA_QUERY),
    @NamedQuery(name = LIST_PROCESSOS_COMUNICACAO_SEM_CUMPRIMENTO, query = LIST_PROCESSOS_COMUNICACAO_SEM_CUMPRIMENTO_QUERY)
})
@Cacheable
public class Processo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Column(name = ID_PROCESSO, unique = true, nullable = false)
    private Integer idProcesso;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_USUARIO_CADASTRO_PROCESSO)
    private UsuarioLogin usuarioCadastro;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "id_processo_pai", nullable = true)
    private Processo processoPai;
    
    @OneToMany(mappedBy = "processoPai", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE })
    private List<Processo> processosFilhos;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_localizacao", nullable = false)
    private Localizacao localizacao;
    
    @NotNull
    @Size(max = NUMERACAO_PROCESSO)
    @Column(name = NUMERO_PROCESSO, nullable = false, length = NUMERACAO_PROCESSO)
    private String numeroProcesso;
    
    @Column(name = "nr_tempo_gasto")
    private Integer tempoGasto;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_prioridade_processo", nullable = true)
    private PrioridadeProcesso prioridadeProcesso;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "st_prazo", nullable = false)
    private SituacaoPrazoEnum situacaoPrazo;
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = DATA_INICIO, nullable = false)
    private Date dataInicio;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = DATA_FIM)
    private Date dataFim;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_CAIXA)
    private Caixa caixa;

    @Column(name = ID_JBPM)
    private Long idJbpm;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_natureza_categoria_fluxo", nullable = false)
    private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
    
    @OneToMany(mappedBy = "processo", fetch = FetchType.LAZY)
    private List<ProcessoTarefa> processoTarefaList = new ArrayList<ProcessoTarefa>(0);
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = PROCESSO_ATTRIBUTE)
    @OrderBy("dataInclusao DESC")
    private List<Documento> documentoList = new ArrayList<Documento>(0);
    
    @OneToMany(mappedBy = "processo", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE })
    @OrderBy(value = "ds_caminho_absoluto")
    private List<ParticipanteProcesso> participantes = new ArrayList<>(0);
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "processo", cascade = {CascadeType.REMOVE})
    private List<MetadadoProcesso> metadadoProcessoList = new ArrayList<>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "processo", cascade = {CascadeType.REMOVE})
    private List<Pasta> pastaList = new ArrayList<>();
    
    
    @PrePersist
    private void prePersist() {
    	if (idProcesso == null) {
    		Integer generatedId = CustomIdGenerator.create(ProcessoQuery.SEQUENCE_PROCESSO).nextValue().intValue();
    		setIdProcesso(generatedId);
    		setNumeroProcesso(getIdProcesso().toString());
    	}
    }
        
    
    public List<Processo> getFilhos() {
		return processosFilhos;
	}


	public void setFilhos(List<Processo> filhos) {
		this.processosFilhos = filhos;
	}


	public Integer getIdProcesso() {
		return idProcesso;
	}

	public void setIdProcesso(Integer idProcesso) {
		this.idProcesso = idProcesso;
	}

	public UsuarioLogin getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public void setUsuarioCadastro(UsuarioLogin usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	public Processo getProcessoPai() {
		return processoPai;
	}

	public void setProcessoPai(Processo processoPai) {
		this.processoPai = processoPai;
	}

	public Localizacao getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}

	public String getNumeroProcesso() {
		return numeroProcesso;
	}
	
	public String getNumeroProcessoRoot() {
		return getProcessoRoot().getNumeroProcesso();
	}

	public void setNumeroProcesso(String numeroProcesso) {
		this.numeroProcesso = numeroProcesso;
	}

	public Integer getTempoGasto() {
		return tempoGasto;
	}

	public void setTempoGasto(Integer tempoGasto) {
		this.tempoGasto = tempoGasto;
	}

	public Float getPorcentagem(){
		return getTempoGasto().floatValue()/getNaturezaCategoriaFluxo().getFluxo().getQtPrazo();
	}
	
	public PrioridadeProcesso getPrioridadeProcesso() {
		return prioridadeProcesso;
	}

	public void setPrioridadeProcesso(PrioridadeProcesso prioridadeProcesso) {
		this.prioridadeProcesso = prioridadeProcesso;
	}

	public SituacaoPrazoEnum getSituacaoPrazo() {
		return situacaoPrazo;
	}

	public void setSituacaoPrazo(SituacaoPrazoEnum situacaoPrazo) {
		this.situacaoPrazo = situacaoPrazo;
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

	public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() {
		return naturezaCategoriaFluxo;
	}

	public void setNaturezaCategoriaFluxo(NaturezaCategoriaFluxo naturezaCategoriaFluxo) {
		this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
	}

	public List<ProcessoTarefa> getProcessoTarefaList() {
		return processoTarefaList;
	}

	public void setProcessoTarefaList(List<ProcessoTarefa> processoTarefaList) {
		this.processoTarefaList = processoTarefaList;
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
	
	public List<MetadadoProcesso> getMetadadoProcessoList() {
		return metadadoProcessoList;
	}

	public void setMetadadoProcessoList(List<MetadadoProcesso> metadadoProcessoList) {
		this.metadadoProcessoList = metadadoProcessoList;
	}
	
	public List<Pasta> getPastaList() {
		return pastaList;
	}

	public void setPastaList(List<Pasta> pastaList) {
		this.pastaList = pastaList;
	}

	public boolean hasPartes(){
    	return naturezaCategoriaFluxo.getNatureza().getHasPartes();
    }
	
	@Transient
	public MetadadoProcesso getMetadado(MetadadoProcessoDefinition metadadoProcessoDefinition) {
		for (MetadadoProcesso metadadoProcesso : getMetadadoProcessoList()) {
			if (metadadoProcessoDefinition.getMetadadoType().equals(metadadoProcesso.getMetadadoType())) {
				return metadadoProcesso;
			}
		}
		return null;
	}
	
	@Transient 
	public Processo getProcessoRoot() {
		Processo processo = this;
		while (processo.getProcessoPai() != null) {
			processo = processo.getProcessoPai();
		}
		return processo;
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
