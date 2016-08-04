package br.com.infox.epp.processo.documento.entity;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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

import org.hibernate.annotations.Type;
import org.jboss.seam.util.Strings;

import br.com.infox.constants.LengthConstants;
import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.util.ArrayUtil;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.assinatura.entity.RegistroAssinaturaSuficiente;
import br.com.infox.epp.processo.documento.query.DocumentoBinQuery;
import br.com.infox.epp.processo.documento.service.DocumentoBinService;
import br.com.infox.epp.processo.marcador.Marcador;
import br.com.infox.hibernate.UUIDGenericType;

@Entity
@Table(name = DocumentoBin.TABLE_NAME)
@NamedQueries({
    @NamedQuery(name = DocumentoBinQuery.GET_BY_UUID, query = DocumentoBinQuery.GET_BY_UUID_QUERY),
    @NamedQuery(name = DocumentoBinQuery.GET_DOCUMENTOS_NAO_SUFICIENTEMENTE_ASSINADOS, query = DocumentoBinQuery.GET_DOCUMENTOS_NAO_SUFICIENTEMENTE_ASSINADOS_QUERY),
})
public class DocumentoBin implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static final float BYTES_IN_A_KILOBYTE = 1024f;

    public static final String TABLE_NAME = "tb_documento_bin";
    
    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = "DocumentoBinGenerator", sequenceName = "sq_documento_bin")
    @GeneratedValue(generator = "DocumentoBinGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_documento_bin", unique = true, nullable = false)
    private Integer id;
    
    @Size(max = LengthConstants.DESCRICAO_MINIMA)
    @Column(name = "ds_extensao", length = LengthConstants.DESCRICAO_MINIMA)
    private String extensao;
    
    @Column(name = "ds_modelo_documento")
    private String modeloDocumento;
    
    @Size(max = LengthConstants.DESCRICAO_MD5)
    @Column(name = "ds_md5_documento", length = LengthConstants.DESCRICAO_MD5)
    private String md5Documento;
    
    @Size(max = LengthConstants.DESCRICAO_NOME_ARQUIVO)
    @Column(name = "nm_arquivo", length = LengthConstants.DESCRICAO_NOME_ARQUIVO)
    private String nomeArquivo;
    
    @Column(name = "nr_tamanho")
    private Integer size;
    
    @NotNull
    @Column(name = "dt_inclusao", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInclusao = new Date();
    
    @Column(name = "ds_uuid")
    @Type(type = UUIDGenericType.TYPE_NAME)
    private UUID uuid = UUID.randomUUID();
    
    @Column(name = "in_minuta")
    @NotNull
    private Boolean minuta = Boolean.TRUE;
    
    @NotNull
    @Column(name="in_assin_sufic")
    private Boolean suficientementeAssinado = Boolean.FALSE;
    
    @Column(name="dt_assin_sufic", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataSuficientementeAssinado;
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinTable(name = "tb_marcador_documento_bin", 
            joinColumns=@JoinColumn(name="id_documento_bin", referencedColumnName="id_documento_bin"),
            inverseJoinColumns=@JoinColumn(name="id_marcador", referencedColumnName="id_marcador"))
    @OrderBy(value = "cd_marcador")
    private Set<Marcador> marcadores = new HashSet<>(1);
    
    @OneToMany(fetch= FetchType.LAZY, mappedBy="documentoBin", cascade = {CascadeType.REMOVE})
    private List<RegistroAssinaturaSuficiente> registrosAssinaturaSuficiente = new ArrayList<>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "documentoBin")
    private List<Documento> documentoList = new ArrayList<>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "documentoBin")
    private List<DocumentoTemporario> documentoTemporarioList = new ArrayList<>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "documentoBin", cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<AssinaturaDocumento> assinaturas = new ArrayList<>();
    
    @Transient
    private byte[] processoDocumento;
    
    @Transient
	private DocumentoBinService documentoBinService;

	@Column(name="tp_documento_externo")
	private String tipoDocumentoExterno;
	
	@Size(max=1000)
	@Column(name="id_documento_externo")
	private String idDocumentoExterno;

	public String getTipoDocumentoExterno() {
		return tipoDocumentoExterno;
	}

	public void setTipoDocumentoExterno(String tipoDocumentoExterno) {
		this.tipoDocumentoExterno = tipoDocumentoExterno;
	}

	public String getIdDocumentoExterno() {
		return idDocumentoExterno;
	}

    public void setIdDocumentoExterno(String idDocumentoExterno) {
		this.idDocumentoExterno = idDocumentoExterno;
	}

	@PrePersist
    private void prePersist() {
    	if (getExtensao() != null) {
    		setMinuta(false);
    	}
    }
    
    @PreUpdate
    private void preUpdate() {
    	if (getModeloDocumento() != null){
    		setMd5Documento(MD5Encoder.encode(getModeloDocumento()));
    	}
    }
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getExtensao() {
		return extensao;		
	}

	public void setExtensao(String extensao) {
		this.extensao = extensao;
	}

	public String getModeloDocumento() {
		return modeloDocumento;
	}

	public void setModeloDocumento(String modeloDocumento) {
		this.modeloDocumento = modeloDocumento;
	}

	public String getMd5DocumentoAtributo() {
		return md5Documento;
	}
	
	public String getMd5Documento() {
		if(getId() == null) {
			return md5Documento;
		}				
		return getDocumentoBinService().getHash(getId());
	}

	public void setMd5Documento(String md5Documento) {
		this.md5Documento = md5Documento;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}
	
	public Integer getSizeAtributo() {
		return size;
	}

	public Integer getSize() {
		if(getId() == null) {
			return size;
		}				
		if(size == null)
			return null;
		return getDocumentoBinService().getSize(getId());
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Date getDataInclusao() {
		return dataInclusao;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}

	public List<Documento> getDocumentoList() {
		return documentoList;
	}

	public void setDocumentoList(List<Documento> documentoList) {
		this.documentoList = documentoList;
	}
	
    public List<DocumentoTemporario> getDocumentoTemporarioList() {
        return documentoTemporarioList;
    }

    public void setDocumentoTemporarioList(List<DocumentoTemporario> documentoTemporarioList) {
        this.documentoTemporarioList = documentoTemporarioList;
    }

    public DocumentoBinService getDocumentoBinService() {
		if(documentoBinService == null) {
			documentoBinService = BeanManager.INSTANCE.getReference(DocumentoBinService.class);
		}
		return documentoBinService; 		
	}

	public List<AssinaturaDocumento> getAssinaturasAtributo() {
		return assinaturas;
	}
	
	public List<AssinaturaDocumento> getAssinaturas() {
		if(getId() == null) {
			return getAssinaturasAtributo();
		}		
		return getDocumentoBinService().carregarAssinaturas(getId());
	}

	public void setAssinaturas(List<AssinaturaDocumento> assinaturas) {
		this.assinaturas = assinaturas;
	}

	public byte[] getProcessoDocumento() {
        return ArrayUtil.copyOf(processoDocumento);
    }

    public void setProcessoDocumento(byte[] processoDocumento) {
        this.processoDocumento = ArrayUtil.copyOf(processoDocumento);
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
    
    public Boolean isMinuta() {
		return minuta;
	}
    
    public Boolean getMinuta(){
        return minuta;
    }
    
    public void setMinuta(Boolean minuta) {
		this.minuta = minuta;
	}
    
    public Boolean getSuficientementeAssinado() {
		return suficientementeAssinado;
	}

	public void setSuficientementeAssinado(Boolean suficientementeAssinado) {
		this.suficientementeAssinado = suficientementeAssinado;
	}

	public Date getDataSuficientementeAssinado() {
		return dataSuficientementeAssinado;
	}

	public void setDataSuficientementeAssinado(Date dataSuficientementeAssinado) {
		this.dataSuficientementeAssinado = dataSuficientementeAssinado;
	}
	
	public List<Marcador> getMarcadoresList(){
		if(this.marcadores == null || this.marcadores.isEmpty())
    		return new ArrayList<Marcador>();
    	List<Marcador> marcadoresList = new ArrayList<Marcador>();
    	marcadoresList.addAll(marcadores);
    	return marcadoresList;
	}
	
	public Set<Marcador> getMarcadores() {
        return marcadores;
    }

    public void setMarcadores(Set<Marcador> marcadores) {
        this.marcadores = marcadores;
    }

    public List<RegistroAssinaturaSuficiente> getRegistrosAssinaturaSuficiente() {
		return registrosAssinaturaSuficiente;
	}

	public void setRegistrosAssinaturaSuficiente(
			List<RegistroAssinaturaSuficiente> registrosAssinaturaSuficiente) {
		this.registrosAssinaturaSuficiente = registrosAssinaturaSuficiente;
	}
	
	@Transient
	public boolean isAssinadoPor(UsuarioPerfil usuarioPerfil) {
        if (getAssinaturas() == null || getAssinaturas().isEmpty()) return false;
        for (AssinaturaDocumento assinatura : getAssinaturas()) {
            if (usuarioPerfil.getPerfilTemplate().getPapel().equals(assinatura.getPapel())
            		&& assinatura.getPessoaFisica().equals(usuarioPerfil.getUsuarioLogin().getPessoaFisica())) {
                return true;
            }
        }
        return false;
	}

	@Override
    public String toString() {
        return isBinario() ? nomeArquivo : md5Documento;
    }

    @Transient
    public boolean isBinario() {
        return !Strings.isEmpty(getExtensao()) && (size != null &&  size.intValue() > 0 ) ;
    }

    @Transient
    public String getSizeFormatado() {
    	Integer size = getSize();
        if (size != null && size > 0) {
            NumberFormat formatter = new DecimalFormat("###,##0.00");
            float sizeF = size / BYTES_IN_A_KILOBYTE;
            return formatter.format(sizeF) + " Kb";
        }
        return "-";
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
		if (!(obj instanceof DocumentoBin))
			return false;
		DocumentoBin other = (DocumentoBin) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}
