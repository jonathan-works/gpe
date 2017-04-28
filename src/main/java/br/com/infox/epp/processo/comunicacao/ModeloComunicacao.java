package br.com.infox.epp.processo.comunicacao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;

import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.processo.comunicacao.query.ModeloComunicacaoQuery;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.entity.Processo;

@Entity
@Table(name = "tb_modelo_comunicacao")
@NamedQueries({
	@NamedQuery(name = ModeloComunicacaoQuery.IS_EXPEDIDA, query = ModeloComunicacaoQuery.IS_EXPEDIDA_QUERY),
	@NamedQuery(name = ModeloComunicacaoQuery.HAS_COMUNICACAO_EXPEDIDA, query = ModeloComunicacaoQuery.HAS_COMUNICACAO_EXPEDIDA_QUERY),
	@NamedQuery(name = ModeloComunicacaoQuery.GET_COMUNICACAO_DESTINATARIO, query = ModeloComunicacaoQuery.GET_COMUNICACAO_DESTINATARIO_QUERY),
	@NamedQuery(name = ModeloComunicacaoQuery.LIST_BY_PROCESSO_ROOT, query = ModeloComunicacaoQuery.LIST_BY_PROCESSO_ROOT_QUERY),
	@NamedQuery(name = ModeloComunicacaoQuery.GET_DOCUMENTOS_MODELO_COMUNICACAO, query = ModeloComunicacaoQuery.GET_DOCUMENTOS_MODELO_COMUNICACAO_QUERY),
	@NamedQuery(name = ModeloComunicacaoQuery.GET_DOCUMENTO_INCLUSO_POR_PAPEL, query = ModeloComunicacaoQuery.GET_DOCUMENTO_INCLUSO_POR_PAPEL_QUERY)
})
@NamedNativeQueries({
	@NamedNativeQuery(name = ModeloComunicacaoQuery.GET_NOME_VARIAVEL_MODELO_COMUNICACAO, query = ModeloComunicacaoQuery.GET_NOME_VARIAVEL_MODELO_COMUNICACAO_QUERY)
})
public class ModeloComunicacao implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "ModeloComunicacaoGenerator", initialValue = 1, allocationSize = 1, sequenceName = "sq_modelo_comunicacao")
	@GeneratedValue(generator = "ModeloComunicacaoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_modelo_comunicacao")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_tipo_comunicacao", nullable = false)
	private TipoComunicacao tipoComunicacao;
	
	@Column(name = "ds_comunicacao")
	private String textoComunicacao;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_classificacao_documento")
	private ClassificacaoDocumento classificacaoComunicacao;
	
	@NotNull
	@Column(name = "in_finalizada", nullable = false)
	private Boolean finalizada = false;
	
	@NotNull
	@Column(name = "in_minuta", nullable = false)
	private boolean minuta = true;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_processo", nullable = false)
	private Processo processo;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_localizacao_resp_assinat", nullable = false)
	private Localizacao localizacaoResponsavelAssinatura;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_perfil_template_assinat")
	private PerfilTemplate perfilResponsavelAssinatura;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_modelo_documento")
	private ModeloDocumento modeloDocumento;
	
	@NotNull
	@Column(name = "in_enviar_relatoria", nullable = false)
	private Boolean enviarRelatoria = Boolean.FALSE;
	
	@Column(name = "cd_task_key", nullable = true)
    private String taskKey;
	
	@Column(name = "cd_marcadores", nullable = true)
	private String codigosMarcadoresString = "";
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_alteracao")
    private Date dataAlteracao;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "modeloComunicacao")
	private List<DestinatarioModeloComunicacao> destinatarios = new ArrayList<>(0);
	
	@OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.LAZY, mappedBy = "modeloComunicacao")
	private List<DocumentoModeloComunicacao> documentos = new ArrayList<>(0);
	
	@Transient
	private List<String> codigosMarcadores = new ArrayList<>(1);

    @PrePersist
    private void prePersist() {
	updateColumnCodigoMarcadoresString();
	setDataAlteracao(DateTime.now().toDate());
    }

    @PreUpdate
    private void preUpdate() {
	updateColumnCodigoMarcadoresString();
	setDataAlteracao(DateTime.now().toDate());
    }

	@PostLoad
	private void postLoad() {
	    loadMarcadoresList();
	}

    private void loadMarcadoresList() {
        if (!StringUtil.isEmpty(codigosMarcadoresString)) {
	        TypeToken<List<String>> typeToken = new TypeToken<List<String>>(){private static final long serialVersionUID = 1L;};
	        codigosMarcadores = new GsonBuilder().create().fromJson(codigosMarcadoresString, typeToken.getType());
	    }
    }
	
	private void updateColumnCodigoMarcadoresString() {
	    if (codigosMarcadores != null && !codigosMarcadores.isEmpty()) {
	        this.codigosMarcadoresString = new GsonBuilder().create().toJson(codigosMarcadores);
	    }
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TipoComunicacao getTipoComunicacao() {
		return tipoComunicacao;
	}

	public void setTipoComunicacao(TipoComunicacao tipoComunicacao) {
		this.tipoComunicacao = tipoComunicacao;
	}

	public ClassificacaoDocumento getClassificacaoComunicacao() {
		return classificacaoComunicacao;
	}
	
	public void setClassificacaoComunicacao(ClassificacaoDocumento classificacaoComunicacao) {
		this.classificacaoComunicacao = classificacaoComunicacao;
	}

	public Boolean getFinalizada() {
		return finalizada;
	}

	public void setFinalizada(Boolean finalizada) {
		this.finalizada = finalizada;
	}

	public Localizacao getLocalizacaoResponsavelAssinatura() {
		return localizacaoResponsavelAssinatura;
	}

	public void setLocalizacaoResponsavelAssinatura(Localizacao localizacaoResponsavelAssinatura) {
		this.localizacaoResponsavelAssinatura = localizacaoResponsavelAssinatura;
	}

	public PerfilTemplate getPerfilResponsavelAssinatura() {
		return perfilResponsavelAssinatura;
	}

	public void setPerfilResponsavelAssinatura(PerfilTemplate perfilResponsavelAssinatura) {
		this.perfilResponsavelAssinatura = perfilResponsavelAssinatura;
	}

	public List<DestinatarioModeloComunicacao> getDestinatarios() {
		return destinatarios;
	}

	public void setDestinatarios(List<DestinatarioModeloComunicacao> destinatarios) {
		this.destinatarios = destinatarios;
	}

	public List<DocumentoModeloComunicacao> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<DocumentoModeloComunicacao> documentos) {
		this.documentos = documentos;
	}
	
	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}
	
	public ModeloDocumento getModeloDocumento() {
		return modeloDocumento;
	}
	
	public void setModeloDocumento(ModeloDocumento modeloDocumento) {
		this.modeloDocumento = modeloDocumento;
	}
	
	public String getTextoComunicacao() {
		return textoComunicacao;
	}
	
	public void setTextoComunicacao(String textoComunicacao) {
		this.textoComunicacao = textoComunicacao;
	}
	
	public boolean isMinuta() {
		return minuta;
	}
	
	public void setMinuta(boolean minuta) {
		this.minuta = minuta;
	}
	
    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public Boolean getEnviarRelatoria() {
		return enviarRelatoria;
	}

	public void setEnviarRelatoria(Boolean enviarRelatoria) {
		this.enviarRelatoria = enviarRelatoria;
	}
	
    public void setCodigosMarcadores(List<String> codigosMarcadores) {
        this.codigosMarcadores = new ArrayList<>();
        if(codigosMarcadores == null)
        	return ;
        	for (String codigo : codigosMarcadores) {
    			this.codigosMarcadores.add(codigo.toUpperCase());
    		}
        
        updateColumnCodigoMarcadoresString();
    }
    
    public List<String> getCodigosMarcadores() {
        return codigosMarcadores;
    }

    public Date getDataAlteracao() {
	return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
	this.dataAlteracao = dataAlteracao;
    }

    @Transient
	public boolean isDocumentoBinario() {
		return Strings.isNullOrEmpty(getTextoComunicacao());
	}
	
	public boolean isClassificacaoAssinavel(){
    	List<ClassificacaoDocumentoPapel> papeis = getClassificacaoComunicacao().getClassificacaoDocumentoPapelList();
		for (ClassificacaoDocumentoPapel tipoProcessoDocumentoPapel : papeis){
			if (tipoProcessoDocumentoPapel.getTipoAssinatura() == TipoAssinaturaEnum.S || tipoProcessoDocumentoPapel.getTipoAssinatura() == TipoAssinaturaEnum.O ){
				return true;
			}
		}
		return false;
	}
	
	public ModeloComunicacao makeCopy() throws CloneNotSupportedException {
		ModeloComunicacao novoModelo = (ModeloComunicacao) clone();
		novoModelo.setId(null);
		novoModelo.setDestinatarios(new ArrayList<DestinatarioModeloComunicacao>());// não copia os destinatários
		novoModelo.setDocumentos(new ArrayList<DocumentoModeloComunicacao>());
		for (DocumentoModeloComunicacao documentoModeloComunicacao : getDocumentos()) {
			DocumentoModeloComunicacao novoDocumento = documentoModeloComunicacao.makeCopy();
			novoModelo.getDocumentos().add(novoDocumento);
		}
		return novoModelo;
	}
	
	public boolean isModeloTotalmenteExpedido() {
	    boolean totalmenteExpedido = true;
        for (DestinatarioModeloComunicacao destino : getDestinatarios()) {
            if (!destino.getExpedido().booleanValue())
                totalmenteExpedido = false;
        }
        return totalmenteExpedido;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ModeloComunicacao))
			return false;
		ModeloComunicacao other = (ModeloComunicacao) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
}