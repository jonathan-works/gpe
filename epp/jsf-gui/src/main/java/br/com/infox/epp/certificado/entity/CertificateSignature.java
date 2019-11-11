/**
 * 
 */
package br.com.infox.epp.certificado.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import br.com.infox.epp.certificado.enums.CertificateSignatureGroupStatus;
import br.com.infox.hibernate.UUIDGenericType;

/**
 * @author erikliberal
 *
 */
@Entity
@Table(name =CertificateSignature.TABLE_NAME)
public class CertificateSignature {
    
    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME)
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_cert_sign", unique = true, nullable = false)
    private Integer idCertificateSignature;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_sign_grp", nullable=false)
    private CertificateSignatureGroup certificateSignatureGroup;
    @Column(name="ds_cert_chain", nullable=false)
    private String certificateChain;
    @Column(name="ds_signature")
    private String signature;
    @Column(name="ds_signed_data")
    private String signedData;
    @Column(name="ds_uuid")
    private String uuid;
    
    @Enumerated(EnumType.STRING)
    @Column(name="tp_signature")
    private TipoAssinatura signatureType = TipoAssinatura.PKCS7;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tp_status")
    private CertificateSignatureGroupStatus status;
    
    @Column(name="cd_erro")
    private String codigoErro;
    
    @Column(name="ds_erro")
    private String mensagemErro;
    
    @Column(name="ob_sha256")
    private byte[] sha256;
    
    @Column(name="ds_uuid_documento_bin")
    @Type(type = UUIDGenericType.TYPE_NAME)
    private UUID uuidDocumentoBin;
    
    public Integer getIdCertificateSignature() {
        return idCertificateSignature;
    }
    public void setIdCertificateSignature(Integer idCertificateSignature) {
        this.idCertificateSignature = idCertificateSignature;
    }
    public CertificateSignatureGroup getCertificateSignatureGroup() {
        return certificateSignatureGroup;
    }
    public void setCertificateSignatureGroup(CertificateSignatureGroup certificateSignatureGroup) {
        this.certificateSignatureGroup = certificateSignatureGroup;
    }
    public String getCertificateChain() {
        return certificateChain;
    }
    public void setCertificateChain(String certificateChain) {
        this.certificateChain = certificateChain;
    }
    public String getSignature() {
        return signature;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }
    public String getSignedData() {
        return signedData;
    }
    public void setSignedData(String signedData) {
        this.signedData = signedData;
    }
    
    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
    public TipoAssinatura getSignatureType() {
		return signatureType;
	}
    
	public void setSignatureType(TipoAssinatura signatureType) {
		this.signatureType = signatureType;
	}
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idCertificateSignature == null) ? 0 : idCertificateSignature.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CertificateSignature)) {
            return false;
        }
        CertificateSignature other = (CertificateSignature) obj;
        if (idCertificateSignature == null) {
            if (other.idCertificateSignature != null) {
                return false;
            }
        } else if (!idCertificateSignature.equals(other.idCertificateSignature)) {
            return false;
        }
        return true;
    }

    public static final String TABLE_NAME = "tb_cert_sign";
    private static final String SEQUENCE_NAME = "sq_cert_sign";
    private static final String GENERATOR_NAME = "CertificateSignatureGenerator";

	public CertificateSignatureGroupStatus getStatus() {
		return status;
	}
	public void setStatus(CertificateSignatureGroupStatus status) {
		this.status = status;
	}
	public String getCodigoErro() {
		return codigoErro;
	}
	public void setCodigoErro(String codigoErro) {
		this.codigoErro = codigoErro;
	}
	public String getMensagemErro() {
		return mensagemErro;
	}
	public void setMensagemErro(String mensagemErro) {
		this.mensagemErro = mensagemErro;
	}
	public byte[] getSha256() {
		return sha256;
	}
	public void setSha256(byte[] sha256) {
		this.sha256 = sha256;
	}
	public UUID getUuidDocumentoBin() {
		return uuidDocumentoBin;
	}
	public void setUuidDocumentoBin(UUID uuidDocumentoBin) {
		this.uuidDocumentoBin = uuidDocumentoBin;
	}

}
