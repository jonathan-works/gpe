/**
 * 
 */
package br.com.infox.epp.certificado.entity;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.LAZY;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.certificado.enums.CertificateSignatureGroupStatus;
import br.com.infox.epp.certificado.query.CertificateSignatureGroupQuery;

/**
 * @author erikliberal
 *
 */
@Entity
@Table(name = CertificateSignatureGroup.TABLE_NAME)
@NamedQueries({@NamedQuery(name=CertificateSignatureGroupQuery.GET_BY_TOKEN,query=CertificateSignatureGroupQuery.GET_BY_TOKEN_QUERY)})
public class CertificateSignatureGroup {

    public static final String TABLE_NAME = "tb_sign_grp";
    private static final String SEQUENCE_NAME = "sq_sign_grp";
    private static final String GENERATOR_NAME = "CertificateSignatureGroupGenerator";

    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME)
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_sign_grp", unique = true, nullable = false)
    private Integer integer;

    @NotNull
    @Column(name = "ds_token", unique = true, nullable = false)
    private String token;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tp_status", nullable = false)
    private CertificateSignatureGroupStatus status;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_criacao", nullable = false)
    private Date dataCriacao;
    @OneToMany(cascade = { PERSIST, MERGE, REFRESH }, fetch = LAZY, mappedBy="certificateSignatureGroup")
    private List<CertificateSignature> certificateSignatureList;

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CertificateSignatureGroupStatus getStatus() {
        return status;
    }

    public void setStatus(CertificateSignatureGroupStatus status) {
        this.status = status;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public List<CertificateSignature> getCertificateSignatureList() {
        return certificateSignatureList;
    }

    public void setCertificateSignatureList(List<CertificateSignature> certificateSignatureList) {
        this.certificateSignatureList = certificateSignatureList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((integer == null) ? 0 : integer.hashCode());
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
        if (!(obj instanceof CertificateSignatureGroup)) {
            return false;
        }
        CertificateSignatureGroup other = (CertificateSignatureGroup) obj;
        if (integer == null) {
            if (other.integer != null) {
                return false;
            }
        } else if (!integer.equals(other.integer)) {
            return false;
        }
        return true;
    }

}
