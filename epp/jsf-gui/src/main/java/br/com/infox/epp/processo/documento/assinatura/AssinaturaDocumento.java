package br.com.infox.epp.processo.documento.assinatura;

import static br.com.infox.constants.LengthConstants.NOME_ATRIBUTO;
import static br.com.infox.constants.LengthConstants.NOME_PADRAO;
import static br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery.COL_CERT_CHAIN;
import static br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery.COL_DATA_ASSINATURA;
import static br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery.COL_ID_ASSINATURA;
import static br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery.COL_NOME_USUARIO;
import static br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery.COL_SIGNATURE;
import static br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery.SEQUENCE_NAME;
import static br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery.TABLE_NAME;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.certificado.CertificadoFactory;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.certificado.entity.TipoAssinatura;
import br.com.infox.epp.documento.domain.Assinatura;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery;

@Entity
@Table(name = TABLE_NAME)
@NamedQueries({ 
	@NamedQuery(name = AssinaturaDocumentoQuery.LIST_ASSINATURA_DOCUMENTO_BY_DOCUMENTO, query = AssinaturaDocumentoQuery.LIST_ASSINATURA_DOCUMENTO_BY_DOCUMENTO_QUERY) 
})
public class AssinaturaDocumento implements Serializable, Assinatura {
    
	private static final long serialVersionUID = 1L;

	@Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = "AssinaturaDocumentoGenerator", sequenceName = SEQUENCE_NAME)
    @GeneratedValue(generator = "AssinaturaDocumentoGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = COL_ID_ASSINATURA, unique = true, nullable = false)
    private Integer idAssinatura;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa_fisica", nullable = false)
	private PessoaFisica pessoaFisica;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_papel", nullable = false)
	private Papel papel;

	@NotNull
	@Size(max = NOME_ATRIBUTO)
	@Column(name = COL_NOME_USUARIO, nullable = false, length = NOME_ATRIBUTO)
    private String nomeUsuario;
	
	@NotNull
	@Size(max = NOME_PADRAO)
	@Column(name = "nm_usuario_perfil", nullable = false, length = NOME_PADRAO)
    private String nomeUsuarioPerfil;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = COL_DATA_ASSINATURA, nullable = false)
    private Date dataAssinatura;
	
	@NotNull
	@Column(name = COL_SIGNATURE, nullable = false)
    private String signature;
	
	@NotNull
	@Column(name = COL_CERT_CHAIN, nullable = false)
    private String certChain;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_documento_bin", nullable = false)
    private DocumentoBin documentoBin;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "tp_assinatura")
	private TipoAssinaturaEnum tipoAssinatura;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "tp_signature")
	private TipoAssinatura signatureType;

	public AssinaturaDocumento(DocumentoBin documentoBin, UsuarioPerfil usuarioPerfil, String certChain, String signature, TipoAssinatura signatureType) throws CertificadoException {
        this.documentoBin=documentoBin;
        this.pessoaFisica = usuarioPerfil.getUsuarioLogin().getPessoaFisica();
        this.nomeUsuario = CertificadoFactory.createCertificado(certChain).getNome();
        this.papel = usuarioPerfil.getPerfilTemplate().getPapel();
        this.nomeUsuarioPerfil = usuarioPerfil.getPerfilTemplate().getDescricao();
        this.signature = signature;
        this.certChain = certChain;
        this.signatureType = signatureType;
        this.dataAssinatura = new Date();
        List<Documento> documentos = Beans.getReference(DocumentoManager.class).getDocumentosFromDocumentoBin(documentoBin);
        if(documentos != null && !documentos.isEmpty()){
        	List<ClassificacaoDocumentoPapel> cdps = documentos.get(0).getClassificacaoDocumento().getClassificacaoDocumentoPapelList();
            Papel papel = usuarioPerfil.getPerfilTemplate().getPapel();
            for (ClassificacaoDocumentoPapel cdp : cdps) {
            	if (papel.equals(cdp.getPapel())) {
            		this.tipoAssinatura = cdp.getTipoAssinatura();
            	}
            }
        }
    }
    
    public AssinaturaDocumento() {
    }

    public Integer getIdAssinatura() {
        return idAssinatura;
    }

    public void setIdAssinatura(Integer idAssinatura) {
        this.idAssinatura = idAssinatura;
    }

    @Override
    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    @Override
    public Papel getPapel() {
        return papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public Date getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(Date dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getCertChain() {
        return certChain;
    }

    public void setCertChain(String certChain) {
        this.certChain = certChain;
    }

    public DocumentoBin getDocumentoBin() {
		return documentoBin;
	}

	public void setDocumentoBin(DocumentoBin documentoBin) {
		this.documentoBin = documentoBin;
	}

	public String getNomeUsuarioPerfil() {
        return nomeUsuarioPerfil;
    }

    public void setNomeUsuarioPerfil(String nomePerfil) {
        this.nomeUsuarioPerfil = nomePerfil;
    }

    public TipoAssinaturaEnum getTipoAssinatura() {
		return tipoAssinatura;
	}
    
    public void setTipoAssinatura(TipoAssinaturaEnum tipoAssinatura) {
		this.tipoAssinatura = tipoAssinatura;
	}
    
    public TipoAssinatura getSignatureType() {
    	return signatureType;
    }
    
    public void setSignatureType(TipoAssinatura signatureType) {
    	this.signatureType = signatureType;
    }
    
    @Override
    public String toString() {
    	return MessageFormat.format("{0}: {1}", nomeUsuarioPerfil, nomeUsuario);
    }
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getIdAssinatura() == null) ? 0 : getIdAssinatura().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AssinaturaDocumento))
			return false;
		AssinaturaDocumento other = (AssinaturaDocumento) obj;
		if (getIdAssinatura() == null) {
			if (other.getIdAssinatura() != null)
				return false;
		} else if (!getIdAssinatura().equals(other.getIdAssinatura()))
			return false;
		return true;
	}
    
}