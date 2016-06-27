/**
 * 
 */
package br.com.infox.epp.certificado.entity;

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
    private TipoAssinatura signatureType = TipoAssinatura.MD5_ASSINADO;
    
    @Enumerated(EnumType.STRING)
    @Column(name="tp_data")
    private TipoDados dataType;
    
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
	
	public TipoDados getDataType() {
		return dataType;
	}
	public void setDataType(TipoDados dataType) {
		this.dataType = dataType;
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


}
