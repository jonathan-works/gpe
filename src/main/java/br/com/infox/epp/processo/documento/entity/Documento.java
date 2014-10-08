package br.com.infox.epp.processo.documento.entity;

import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_ANEXOS_PUBLICOS;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_ANEXOS_PUBLICOS_QUERY;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO_QUERY;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_DOCUMENTO_BY_PROCESSO;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_DOCUMENTO_BY_PROCESSO_QUERY;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_DOCUMENTO_BY_TASKINSTANCE;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.lIST_DOCUMENTO_BY_TASKINSTANCE_QUERY;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.NEXT_SEQUENCIAL;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.NEXT_SEQUENCIAL_QUERY;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.entity.Processo;

@Entity
@Table(name = Documento.TABLE_NAME)
@NamedQueries({
    @NamedQuery(name = LIST_ANEXOS_PUBLICOS, query = LIST_ANEXOS_PUBLICOS_QUERY),
    @NamedQuery(name = NEXT_SEQUENCIAL, query = NEXT_SEQUENCIAL_QUERY),
    @NamedQuery(name = LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO, query = LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO_QUERY),
    @NamedQuery(name = LIST_DOCUMENTO_BY_PROCESSO, query = LIST_DOCUMENTO_BY_PROCESSO_QUERY),
    @NamedQuery(name = LIST_DOCUMENTO_BY_TASKINSTANCE, query = lIST_DOCUMENTO_BY_TASKINSTANCE_QUERY)
})
@Indexed(index="IndexProcessoDocumento")
public class Documento implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "tb_documento";

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = "DocumentoGenerator", sequenceName = "sq_documento")
    @GeneratedValue(generator = "DocumentoGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_documento", unique = true, nullable = false)
    private Integer id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_classificacao_documento", nullable = false)
    private ClassificacaoDocumento classificacaoDocumento;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "id_documento_bin", nullable = false)
    private DocumentoBin documentoBin;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo", nullable = false)
    private Processo processo;
    
    @NotNull
    @Size(max = LengthConstants.DESCRICAO_PADRAO)
    @Column(name = "ds_documento", nullable = false, length = LengthConstants.DESCRICAO_PADRAO)
    private String descricao;
    
    @Column(name = "nr_documento", nullable = true)
    private Integer numeroDocumento;
    
    @NotNull
    @Column(name = "in_documento_sigiloso", nullable = false)
    private Boolean documentoSigiloso = Boolean.FALSE;
    
    @NotNull
    @Column(name = "in_anexo", nullable = false)
    private Boolean anexo = Boolean.FALSE;
    
    @Column(name = "id_jbpm_task")
    private Long idJbpmTask;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil_template", nullable = true)
    private PerfilTemplate perfilTemplate;
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_inclusao", nullable = false)
    private Date dataInclusao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_inclusao")
    private UsuarioLogin usuarioInclusao;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_alteracao", nullable = false)
    private Date dataAlteracao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_alteracao")
    private UsuarioLogin usuarioAlteracao;
    
    @NotNull
    @Column(name="in_excluido", nullable = false)
    private Boolean excluido = Boolean.FALSE;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "documento", cascade = CascadeType.REMOVE)
    @OrderBy(value="dataAlteracao DESC")
    private List<HistoricoStatusDocumento> historicoStatusDocumentoList = new ArrayList<>();
    
    @PrePersist
    private void prePersist(){
    	if (getPerfilTemplate() == null){
    		setPerfilTemplate(Authenticator.getUsuarioPerfilAtual().getPerfilTemplate());
    	}
    	setDataInclusao(new Date());
    	setUsuarioInclusao(Authenticator.getUsuarioLogado());
    }
    
    @PreUpdate
    private void preUpdate(){
    	setDataAlteracao(new Date());
    	setUsuarioAlteracao(Authenticator.getUsuarioLogado());
    }
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ClassificacaoDocumento getClassificacaoDocumento() {
		return classificacaoDocumento;
	}

	public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
		this.classificacaoDocumento = classificacaoDocumento;
	}

	public DocumentoBin getDocumentoBin() {
		return documentoBin;
	}

	public void setDocumentoBin(DocumentoBin documentoBin) {
		this.documentoBin = documentoBin;
	}

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(Integer numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public Boolean getDocumentoSigiloso() {
		return documentoSigiloso;
	}

	public void setDocumentoSigiloso(Boolean documentoSigiloso) {
		this.documentoSigiloso = documentoSigiloso;
	}

	public Boolean getAnexo() {
		return anexo;
	}

	public void setAnexo(Boolean anexo) {
		this.anexo = anexo;
	}

	public Long getIdJbpmTask() {
		return idJbpmTask;
	}

	public void setIdJbpmTask(Long idJbpmTask) {
		this.idJbpmTask = idJbpmTask;
	}

	public PerfilTemplate getPerfilTemplate() {
		return perfilTemplate;
	}

	public void setPerfilTemplate(PerfilTemplate perfilTemplate) {
		this.perfilTemplate = perfilTemplate;
	}

	public Date getDataInclusao() {
		return dataInclusao;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}

	public UsuarioLogin getUsuarioInclusao() {
		return usuarioInclusao;
	}

	public void setUsuarioInclusao(UsuarioLogin usuarioInclusao) {
		this.usuarioInclusao = usuarioInclusao;
	}
	
	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public UsuarioLogin getUsuarioAlteracao() {
		return usuarioAlteracao;
	}

	public void setUsuarioAlteracao(UsuarioLogin usuarioAlteracao) {
		this.usuarioAlteracao = usuarioAlteracao;
	}

	public Boolean getExcluido() {
		return excluido;
	}

	public void setExcluido(Boolean excluido) {
		this.excluido = excluido;
	}

	public List<HistoricoStatusDocumento> getHistoricoStatusDocumentoList() {
		return historicoStatusDocumentoList;
	}

	public void setHistoricoStatusDocumentoList(
			List<HistoricoStatusDocumento> historicoStatusDocumentoList) {
		this.historicoStatusDocumentoList = historicoStatusDocumentoList;
	}

	public boolean isDocumentoAssinavel(Papel papel){
    	List<ClassificacaoDocumentoPapel> papeis = getClassificacaoDocumento().getClassificacaoDocumentoPapelList();
		for (ClassificacaoDocumentoPapel tipoProcessoDocumentoPapel : papeis){
			if (tipoProcessoDocumentoPapel.getPapel().equals(papel) 
					&& tipoProcessoDocumentoPapel.getTipoAssinatura() != TipoAssinaturaEnum.P){
				return true;
			}
		}
		return false;
    }
    
    public boolean isDocumentoAssinavel(){
    	List<ClassificacaoDocumentoPapel> papeis = getClassificacaoDocumento().getClassificacaoDocumentoPapelList();
		for (ClassificacaoDocumentoPapel tipoProcessoDocumentoPapel : papeis){
			if (tipoProcessoDocumentoPapel.getTipoAssinatura() != TipoAssinaturaEnum.P){
				return true;
			}
		}
		return false;
    }
    
    public boolean isDocumentoAssinado(Papel papel){
    	for(AssinaturaDocumento assinaturaDocumento : getDocumentoBin().getAssinaturas()){
    		if (assinaturaDocumento.getUsuarioPerfil().getPerfilTemplate().getPapel().equals(papel)){
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean hasAssinatura(){
    	return getDocumentoBin().getAssinaturas() != null && 
    			getDocumentoBin().getAssinaturas().size() > 0;
    }
    
    @Transient
    @Field(index = Index.YES, store = Store.NO, name = "texto")
    public String getTextoIndexavel() {
        return getDocumentoBin().getModeloDocumento();
    }

    @Transient
    @Field(index = Index.YES, store = Store.NO, name = "nome")
    public String getNomeIndexavel() {
        return getDescricao();
    }
    
    @Override
    public String toString() {
        return descricao;
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
		if (!(obj instanceof Documento))
			return false;
		Documento other = (Documento) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
    
}
