package br.com.infox.epp.processo.documento.entity;

import static br.com.infox.epp.processo.documento.query.DocumentoTemporarioQuery.LOAD_BY_ID;
import static br.com.infox.epp.processo.documento.query.DocumentoTemporarioQuery.LOAD_BY_ID_QUERY;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.entity.Processo;

@Entity()
@Table(name = "tb_documento_temporario")
@NamedQueries(value = {
        @NamedQuery(name = LOAD_BY_ID, query = LOAD_BY_ID_QUERY),
})
public class DocumentoTemporario implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final String GENERATOR_NAME = "DocumentoTemporarioGenerator";
    private static final String SEQUENCE_NAME = "sq_documento_temporario";
    
    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME)
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_documento_temporario", unique = true, nullable = false)
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo", nullable = false)
    private Processo processo;

    @NotNull
    @Size(max = Documento.TAMANHO_MAX_DESCRICAO_DOCUMENTO)
    @Column(name = "ds_documento", nullable = false, length=Documento.TAMANHO_MAX_DESCRICAO_DOCUMENTO)
    private String descricao;

    @Column(name = "nr_documento")
    private Long numeroDocumento;

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
    
    @ManyToOne
    @JoinColumn(name="id_pasta")
    private Pasta pasta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_localizacao")
    private Localizacao localizacao;

    @PrePersist
    private void prePersist() {
        setDataInclusao(new Date());
        setUsuarioInclusao(Authenticator.getUsuarioLogado());
        setPerfilTemplate(Authenticator.getUsuarioPerfilAtual().getPerfilTemplate());
        setLocalizacao(Authenticator.getLocalizacaoAtual());
    }
    
    @PreUpdate
    private void preUpdate() {
        setUsuarioAlteracao(Authenticator.getUsuarioLogado());
        setDataAlteracao(new Date());
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

    public Long getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(Long numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public String getNumeroAnoDocumento() {
		if (this.numeroDocumento != null && this.dataInclusao != null) {
			return "" + this.getNumeroDocumento() + "/" + new SimpleDateFormat("YYYY").format(this.getDataInclusao()); 
		}
		return null;
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
		if (!(obj instanceof DocumentoTemporario))
			return false;
		DocumentoTemporario other = (DocumentoTemporario) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

    
}