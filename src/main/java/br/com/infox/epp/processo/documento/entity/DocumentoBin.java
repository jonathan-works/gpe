package br.com.infox.epp.processo.documento.entity;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.assinatura.entity.RegistroAssinaturaSuficiente;
import br.com.infox.epp.processo.documento.query.DocumentoBinQuery;
import br.com.infox.hibernate.UUIDGenericType;

@Entity
@Table(name = DocumentoBin.TABLE_NAME)
@NamedQueries({
    @NamedQuery(name = DocumentoBinQuery.GET_BY_UUID, query = DocumentoBinQuery.GET_BY_UUID_QUERY),
    @NamedQuery(name = DocumentoBinQuery.GET_DOCUMENTOS_NAO_SUFICIENTEMENTE_ASSINADOS, query = DocumentoBinQuery.GET_DOCUMENTOS_NAO_SUFICIENTEMENTE_ASSINADOS_QUERY),
})
public class DocumentoBin implements Serializable {

    private static final float BYTES_IN_A_KILOBYTE = 1024f;

    private static final long serialVersionUID = 1L;
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
    
    @NotNull
    @Size(max = LengthConstants.DESCRICAO_MD5)
    @Column(name = "ds_md5_documento", nullable = false, length = LengthConstants.DESCRICAO_MD5)
    private String md5Documento;
    
    @Size(max = LengthConstants.DESCRICAO_NOME_ARQUIVO)
    @Column(name = "nm_arquivo", length = LengthConstants.DESCRICAO_NOME_ARQUIVO)
    private String nomeArquivo;
    
    @Column(name = "nr_tamanho")
    private Integer size;
    
    @NotNull
    @Column(name = "dt_inclusao", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInclusao;
    
    @Column(name = "ds_uuid")
    @Type(type = UUIDGenericType.TYPE_NAME)
    private UUID uuid;
    
    @Column(name = "in_minuta")
    @NotNull
    private Boolean minuta;
    
    @NotNull
    @Column(name="in_assin_sufic")
    private Boolean suficientementeAssinado;
    
    @Column(name="dt_assin_sufic", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataSuficientementeAssinado;
    
    @OneToMany(fetch= FetchType.LAZY, mappedBy="documentoBin")
    private List<RegistroAssinaturaSuficiente> registrosAssinaturaSuficiente;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "documentoBin")
    private List<Documento> documentoList;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "documentoBin", cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<AssinaturaDocumento> assinaturas = new ArrayList<>();
    
    @Transient
    private byte[] processoDocumento;

    public DocumentoBin() {
    	this.minuta = Boolean.TRUE;
    	this.suficientementeAssinado = Boolean.FALSE;
        this.dataInclusao=new Date();
        this.documentoList = new ArrayList<>(0);
        this.assinaturas = new ArrayList<>(0);
        this.registrosAssinaturaSuficiente = new ArrayList<>(0);
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
    
	public boolean isAssinadoPorPapel(Papel papel) {
		for (AssinaturaDocumento assinaturaDocumento : getAssinaturas()) {
			if (assinaturaDocumento.getUsuarioPerfil().getPerfilTemplate().getPapel().equals(papel)) {
				return true;
			}
		}
		return false;
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

	public String getMd5Documento() {
		return md5Documento;
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

	public Integer getSize() {
		return size;
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

	public List<AssinaturaDocumento> getAssinaturas() {
		return assinaturas;
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

	public List<RegistroAssinaturaSuficiente> getRegistrosAssinaturaSuficiente() {
		return registrosAssinaturaSuficiente;
	}

	public void setRegistrosAssinaturaSuficiente(
			List<RegistroAssinaturaSuficiente> registrosAssinaturaSuficiente) {
		this.registrosAssinaturaSuficiente = registrosAssinaturaSuficiente;
	}

	@Override
    public String toString() {
        return isBinario() ? nomeArquivo : md5Documento;
    }

    @Transient
    public boolean isBinario() {
        return !Strings.isEmpty(getExtensao());
    }

    @Transient
    public String getSizeFormatado() {
        if (size != null && size > 0) {
            NumberFormat formatter = new DecimalFormat("###,##0.00");
            float sizeF = size / BYTES_IN_A_KILOBYTE;
            return formatter.format(sizeF) + " Kb";
        }
        return "0 Kb";
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
