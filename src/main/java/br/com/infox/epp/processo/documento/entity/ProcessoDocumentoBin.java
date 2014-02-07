package br.com.infox.epp.processo.documento.entity;

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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import org.jboss.seam.util.Strings;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.core.util.ArrayUtil;
import br.com.infox.epp.access.entity.UsuarioLogin;

@Entity
@Table(name = "tb_processo_documento_bin", schema = "public")
public class ProcessoDocumentoBin implements java.io.Serializable {

    private static final float BYTES_IN_A_KILOBYTE = 1024f;

    private static final long serialVersionUID = 1L;

    private int idProcessoDocumentoBin;
    private UsuarioLogin usuario;
    private String nomeUsuario;
    private String usuarioUltimoAssinar;
    private byte[] processoDocumento;
    private String extensao;
    private String modeloDocumento;
    private String md5Documento;
    private String nomeArquivo;
    private Date dataInclusao = new Date();
    private Integer size;
    private String signature;
    private String certChain;
    private List<ProcessoDocumento> processoDocumentoList = new ArrayList<ProcessoDocumento>(0);

    public ProcessoDocumentoBin() {
    }

    @SequenceGenerator(name = "generator",
            sequenceName = "public.sq_tb_processo_documento_bin")
    @Id
    @GeneratedValue(generator = "generator")
    @Column(name = "id_processo_documento_bin", unique = true, nullable = false)
    @NotNull
    public int getIdProcessoDocumentoBin() {
        return this.idProcessoDocumentoBin;
    }

    public void setIdProcessoDocumentoBin(int idProcessoDocumentoBin) {
        this.idProcessoDocumentoBin = idProcessoDocumentoBin;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    public UsuarioLogin getUsuario() {
        return this.usuario;
    }

    public void setUsuario(UsuarioLogin usuario) {
        this.usuario = usuario;
    }

    @Column(name = "ds_extensao", length = LengthConstants.DESCRICAO_MINIMA)
    @Size(max = LengthConstants.DESCRICAO_MINIMA)
    public String getExtensao() {
        return this.extensao;
    }

    public void setExtensao(String extensao) {
        this.extensao = extensao;
    }

    @Column(name = "ds_modelo_documento")
    public String getModeloDocumento() {
        return this.modeloDocumento;
    }

    public void setModeloDocumento(String modeloDocumento) {
        this.modeloDocumento = modeloDocumento;
    }

    @Column(name = "ds_md5_documento", nullable = false,
            length = LengthConstants.DESCRICAO_MD5)
    @NotNull
    @Size(max = LengthConstants.DESCRICAO_MD5)
    public String getMd5Documento() {
        return this.md5Documento;
    }

    public void setMd5Documento(String md5Documento) {
        this.md5Documento = md5Documento;
    }

    @Column(name = "nm_arquivo",
            length = LengthConstants.DESCRICAO_NOME_ARQUIVO)
    @Size(max = LengthConstants.DESCRICAO_NOME_ARQUIVO)
    public String getNomeArquivo() {
        return this.nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_inclusao", nullable = false)
    @NotNull
    public Date getDataInclusao() {
        return this.dataInclusao;
    }

    public void setDataInclusao(Date dataInclusao) {
        this.dataInclusao = dataInclusao;
    }

    @Column(name = "ds_signature")
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Column(name = "ds_cert_chain")
    public String getCertChain() {
        return certChain;
    }

    public void setCertChain(String certChain) {
        this.certChain = certChain;
    }

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
        CascadeType.REFRESH }, fetch = FetchType.LAZY,
            mappedBy = "processoDocumentoBin")
    public List<ProcessoDocumento> getProcessoDocumentoList() {
        return this.processoDocumentoList;
    }

    public void setProcessoDocumentoList(
            List<ProcessoDocumento> processoDocumentoList) {
        this.processoDocumentoList = processoDocumentoList;
    }

    @Override
    public String toString() {
        return isBinario() ? nomeArquivo : md5Documento;
    }

    @Transient
    public byte[] getProcessoDocumento() {
        return ArrayUtil.copyOf(processoDocumento);
    }

    public void setProcessoDocumento(byte[] processoDocumento) {
        this.processoDocumento = ArrayUtil.copyOf(processoDocumento);
    }

    @Column(name = "nr_tamanho")
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
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

    @Column(name = "ds_nome_usuario_ultimo_assinar",
            length = LengthConstants.DESCRICAO_PADRAO)
    @Size(max = LengthConstants.DESCRICAO_PADRAO)
    public String getUsuarioUltimoAssinar() {
        return usuarioUltimoAssinar;
    }

    public void setUsuarioUltimoAssinar(String usuarioUltimoAssinar) {
        this.usuarioUltimoAssinar = usuarioUltimoAssinar;
    }

    @Column(name = "ds_nome_usuario", length = LengthConstants.DESCRICAO_PADRAO)
    @Size(max = LengthConstants.DESCRICAO_PADRAO)
    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ProcessoDocumentoBin)) {
            return false;
        }
        ProcessoDocumentoBin other = (ProcessoDocumentoBin) obj;
        if (getIdProcessoDocumentoBin() != other.getIdProcessoDocumentoBin()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getIdProcessoDocumentoBin();
        return result;
    }
}
