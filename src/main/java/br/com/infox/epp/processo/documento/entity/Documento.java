package br.com.infox.epp.processo.documento.entity;

import static br.com.infox.epp.processo.documento.query.DocumentoQuery.DOCUMENTOS_POR_CLASSIFICACAO_DOCUMENTO_ORDENADOS_POR_DATA_INCLUSAO;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.DOCUMENTOS_POR_CLASSIFICACAO_DOCUMENTO_ORDENADOS_POR_DATA_INCLUSAO_QUERY;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.DOCUMENTOS_SESSAO_ANEXAR;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.DOCUMENTOS_SESSAO_ANEXAR_QUERY;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_DOCUMENTO_BY_PROCESSO;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_DOCUMENTO_BY_PROCESSO_QUERY;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_DOCUMENTO_BY_TASKINSTANCE;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_DOCUMENTO_MINUTA_BY_PROCESSO;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.LIST_DOCUMENTO_MINUTA_BY_PROCESSO_QUERY;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.NEXT_SEQUENCIAL;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.NEXT_SEQUENCIAL_QUERY;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.TOTAL_DOCUMENTOS_PROCESSO;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.TOTAL_DOCUMENTOS_PROCESSO_QUERY;
import static br.com.infox.epp.processo.documento.query.DocumentoQuery.lIST_DOCUMENTO_BY_TASKINSTANCE_QUERY;

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

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.publicacao.PublicacaoDocumento;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;

@Entity
@Table(name = Documento.TABLE_NAME)
@NamedQueries({
    @NamedQuery(name = NEXT_SEQUENCIAL, query = NEXT_SEQUENCIAL_QUERY),
    @NamedQuery(name = LIST_DOCUMENTO_BY_PROCESSO, query = LIST_DOCUMENTO_BY_PROCESSO_QUERY),
    @NamedQuery(name = LIST_DOCUMENTO_MINUTA_BY_PROCESSO, query = LIST_DOCUMENTO_MINUTA_BY_PROCESSO_QUERY),
    @NamedQuery(name = LIST_DOCUMENTO_BY_TASKINSTANCE, query = lIST_DOCUMENTO_BY_TASKINSTANCE_QUERY),
    @NamedQuery(name = TOTAL_DOCUMENTOS_PROCESSO, query = TOTAL_DOCUMENTOS_PROCESSO_QUERY),
    @NamedQuery(name = DOCUMENTOS_SESSAO_ANEXAR, query = DOCUMENTOS_SESSAO_ANEXAR_QUERY),
    @NamedQuery(name = DOCUMENTOS_POR_CLASSIFICACAO_DOCUMENTO_ORDENADOS_POR_DATA_INCLUSAO, query = DOCUMENTOS_POR_CLASSIFICACAO_DOCUMENTO_ORDENADOS_POR_DATA_INCLUSAO_QUERY)
})
@Indexed(index="IndexProcessoDocumento")
public class Documento implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    public static final int TAMANHO_MAX_DESCRICAO_DOCUMENTO = 260;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_documento_bin", nullable = false)
    private DocumentoBin documentoBin;
    
    @NotNull
    @Size(max = TAMANHO_MAX_DESCRICAO_DOCUMENTO)
    @Column(name = "ds_documento", nullable = false, length = TAMANHO_MAX_DESCRICAO_DOCUMENTO)
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
    private Boolean excluido;
    
    @NotNull
    @ManyToOne
    @JoinColumn(name="id_pasta", nullable = false)
    private Pasta pasta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_localizacao")
    private Localizacao localizacao;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "documento", cascade = CascadeType.REMOVE)
    @OrderBy(value="dataAlteracao DESC")
    private List<HistoricoStatusDocumento> historicoStatusDocumentoList = new ArrayList<>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "documento")
    @OrderBy(value="dataPublicacao DESC")
    private List<PublicacaoDocumento> publicacoes = new ArrayList<>();
    
    @PrePersist
    private void prePersist(){
    	UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
		if (usuarioPerfil == null) {
			UsuarioLogin usuario = getUsuarioSistema();
			// Assumindo que o usuário do sistema só possui um perfil associado a ele, o perfil Sistema
			// com papel sistema e localização raiz
			usuarioPerfil = usuario.getUsuarioPerfilList().get(0);
		}
		
    	if (getPerfilTemplate() == null){
    		setPerfilTemplate(usuarioPerfil.getPerfilTemplate());
    	}
    	if (getLocalizacao() == null) {
    		setLocalizacao(usuarioPerfil.getLocalizacao());
    	}
    	if (getDataInclusao() == null) {
    		setDataInclusao(new Date());
    	}
    	if (getUsuarioInclusao() == null) {
    		setUsuarioInclusao(getUsuarioLogadoOuSistema());
    	}
    	setExcluido(Boolean.FALSE);
    }
    
    @PreUpdate
    private void preUpdate(){
    	setDataAlteracao(new Date());
    	setUsuarioAlteracao(getUsuarioLogadoOuSistema());
    }
    
    private UsuarioLogin getUsuarioLogadoOuSistema() {
    	UsuarioLogin usuario = Authenticator.getUsuarioLogado();
    	if (usuario == null) {
    		usuario = getUsuarioSistema();
    	}
    	return usuario;
    }

	private UsuarioLogin getUsuarioSistema() {
		UsuarioLoginManager usuarioLoginManager = BeanManager.INSTANCE.getReference(UsuarioLoginManager.class);
		return usuarioLoginManager.getUsuarioSistema();
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

	public Pasta getPasta() {
	    return pasta;
	}
	
	public void setPasta(Pasta pasta) {
	    this.pasta = pasta;
	}
	
	public Localizacao getLocalizacao() {
		return localizacao;
	}
	
	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}

    public boolean isDocumentoAssinavel(Papel papel){
		if (getDocumentoBin() == null || getClassificacaoDocumento() == null) {
			return false;
		}
		if(getPasta().getProcesso() != null && getPasta().getProcesso().isFinalizado()) {
    		return false;
    	}
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
    	if (getDocumentoBin() == null || getDocumentoBin().isMinuta()) {
			return false;
		}
    	if(getPasta().getProcesso() != null && getPasta().getProcesso().isFinalizado()) {
    		return false;
    	}
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
    		if (assinaturaDocumento.getPapel().equals(papel)){
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean isDocumentoAssinado(UsuarioLogin usuarioLogin){
    	for(AssinaturaDocumento assinaturaDocumento : getDocumentoBin().getAssinaturas()){
    		if (assinaturaDocumento.getPessoaFisica().equals(usuarioLogin.getPessoaFisica())){
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean hasAssinaturaSuficiente() {
    	List<ClassificacaoDocumentoPapel> papeis = getClassificacaoDocumento().getClassificacaoDocumentoPapelList();
    	for (ClassificacaoDocumentoPapel classificacaoDocumentoPapel : papeis) {
    		if (classificacaoDocumentoPapel.getTipoAssinatura() == TipoAssinaturaEnum.S
    				&& isDocumentoAssinado(classificacaoDocumentoPapel.getPapel())) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean hasAssinatura(){
    	return getDocumentoBin() != null 
    				&& getDocumentoBin().getAssinaturas() != null 
    				&& getDocumentoBin().getAssinaturas().size() > 0;
    }
    
    public boolean isAssinaturaObrigatoria(Papel papel) {
    	List<ClassificacaoDocumentoPapel> papeis = getClassificacaoDocumento().getClassificacaoDocumentoPapelList();
    	boolean existeObrigatoria=false;
    	TipoAssinaturaEnum tipoAssinaturaEncontrado=null;
        for (ClassificacaoDocumentoPapel classificacaoDocumentoPapel : papeis) {
            switch (classificacaoDocumentoPapel.getTipoAssinatura()) {
            case O:
                existeObrigatoria |= true;
                break;
            default:
                break;
            }
            if (classificacaoDocumentoPapel.getPapel().equals(papel)) {
                tipoAssinaturaEncontrado = classificacaoDocumentoPapel.getTipoAssinatura();
            }
        }
    	return TipoAssinaturaEnum.O.equals(tipoAssinaturaEncontrado) || (TipoAssinaturaEnum.S.equals(tipoAssinaturaEncontrado) && !existeObrigatoria);
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
	
	public Documento makeCopy() throws CloneNotSupportedException {
		Documento cDocumento = (Documento) clone();
		cDocumento.setId(null);
		List<HistoricoStatusDocumento> cList = new ArrayList<>();
		for (HistoricoStatusDocumento hsd : cDocumento.getHistoricoStatusDocumentoList()) {
			HistoricoStatusDocumento cHistorico = hsd.makeCopy();
			cHistorico.setDocumento(cDocumento);
			cList.add(cHistorico);
		}
		cDocumento.setHistoricoStatusDocumentoList(cList);
		return cDocumento;
	}

	// TODO corrigir para integrar epp 2.14
//	@SuppressWarnings("unchecked")
//	public List<PublicacaoDocumento> getPublicacoes() {
//		return Collections.unmodifiableList(publicacoes);
//	}
}
