package br.com.infox.epp.processo.documento.entity;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jboss.seam.util.Strings;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.core.util.ArrayUtil;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;

@Entity
@Table(name = ProcessoDocumentoBin.TABLE_NAME)
public class ProcessoDocumentoBin implements Serializable {

    private static final float BYTES_IN_A_KILOBYTE = 1024f;

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "tb_processo_documento_bin";
    
    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = "generator", sequenceName = "sq_tb_processo_documento_bin")
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_processo_documento_bin", unique = true, nullable = false)
    private Integer idProcessoDocumentoBin;
    
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
    
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY, mappedBy = "processoDocumentoBin")
    private List<Documento> documentoList;
    
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy = "processoDocumentoBin")
    private List<AssinaturaDocumento> assinaturas;
    
    @Transient
    private byte[] processoDocumento;

    public ProcessoDocumentoBin() {
        this.dataInclusao=new Date();
        this.documentoList = new ArrayList<>(0);
        this.assinaturas = new ArrayList<>(0);
    }
    
	public Integer getIdProcessoDocumentoBin() {
		return idProcessoDocumentoBin;
	}

	public void setIdProcessoDocumentoBin(Integer idProcessoDocumentoBin) {
		this.idProcessoDocumentoBin = idProcessoDocumentoBin;
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
    
    @Override
    public String toString() {
        return isBinario() ? nomeArquivo : md5Documento;
    }

    @Transient
    public boolean isBinario() {
        return !Strings.isEmpty(nomeArquivo);
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
		result = prime
				* result
				+ ((idProcessoDocumentoBin == null) ? 0
						: idProcessoDocumentoBin.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ProcessoDocumentoBin))
			return false;
		ProcessoDocumentoBin other = (ProcessoDocumentoBin) obj;
		if (idProcessoDocumentoBin == null) {
			if (other.idProcessoDocumentoBin != null)
				return false;
		} else if (!idProcessoDocumentoBin.equals(other.idProcessoDocumentoBin))
			return false;
		return true;
	}

}
