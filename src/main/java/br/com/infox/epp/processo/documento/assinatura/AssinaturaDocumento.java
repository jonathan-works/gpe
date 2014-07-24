package br.com.infox.epp.processo.documento.assinatura;

import static br.com.infox.core.constants.LengthConstants.NOME_ATRIBUTO;
import static br.com.infox.core.constants.LengthConstants.NOME_PADRAO;
import static br.com.infox.epp.access.query.PerfilQuery.COL_ID_PERFIL;
import static br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery.COL_CERT_CHAIN;
import static br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery.COL_DATA_ASSINATURA;
import static br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery.COL_ID_ASSINATURA;
import static br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery.COL_NOME_PERFIL;
import static br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery.COL_NOME_USUARIO;
import static br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery.COL_SIGNATURE;
import static br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery.SEQUENCE_NAME;
import static br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery.TABLE_NAME;

import java.io.Serializable;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.access.entity.Perfil;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.query.UsuarioLoginQuery;
import br.com.infox.epp.processo.documento.dao.AssinaturaDocumentoDAO;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.query.AssinaturaDocumentoQuery;
import br.com.infox.seam.util.ComponentUtil;

@Entity
@Table(name = TABLE_NAME)
@NamedQueries({ @NamedQuery(name = AssinaturaDocumentoQuery.LIST_ASSINATURA_DOCUMENTO_BY_PROCESSO_DOCUMENTO, query = AssinaturaDocumentoQuery.LIST_ASSINATURA_DOCUMENTO_BY_PROCESSO_DOCUMENTO_QUERY) })
public class AssinaturaDocumento implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idAssinatura;
    private UsuarioLogin usuario;
    private Perfil perfil;
    private String nomeUsuario;
    private String nomePerfil;
    private Date dataAssinatura;
    private String signature;
    private String certChain;
    private ProcessoDocumentoBin processoDocumentoBin;

    public AssinaturaDocumento(ProcessoDocumentoBin processoDocumentoBin, UsuarioPerfil usuarioPerfil, String certChain, String signature) {
        this.processoDocumentoBin=processoDocumentoBin;
        this.usuario = usuarioPerfil.getUsuarioLogin();
        this.nomeUsuario = this.usuario.getNomeUsuario();
        this.perfil = usuarioPerfil.getPerfil();
        this.nomePerfil = this.perfil.getDescricao();
        this.signature = signature;
        this.certChain = certChain;
    }
    
    public AssinaturaDocumento() {
    }

    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = "generator", sequenceName = SEQUENCE_NAME)
    @Id
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    @Column(name = COL_ID_ASSINATURA, unique = true, nullable = false)
    @NotNull
    public Integer getIdAssinatura() {
        return idAssinatura;
    }

    public void setIdAssinatura(Integer idAssinatura) {
        this.idAssinatura = idAssinatura;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = UsuarioLoginQuery.ID_USUARIO, nullable = false)
    @NotNull
    public UsuarioLogin getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioLogin usuario) {
        this.usuario = usuario;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = COL_ID_PERFIL, nullable = false)
    @NotNull
    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    @Column(name = COL_NOME_USUARIO, nullable = false, length = NOME_ATRIBUTO)
    @Size(max = NOME_ATRIBUTO)
    @NotNull
    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = COL_DATA_ASSINATURA, nullable = false)
    @NotNull
    public Date getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(Date dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    @Column(name = COL_SIGNATURE, nullable = false)
    @NotNull
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Column(name = COL_CERT_CHAIN, nullable = false)
    @NotNull
    public String getCertChain() {
        return certChain;
    }

    public void setCertChain(String certChain) {
        this.certChain = certChain;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo_documento_bin", nullable = false)
    @NotNull
    public ProcessoDocumentoBin getProcessoDocumentoBin() {
        return processoDocumentoBin;
    }

    public void setProcessoDocumentoBin(
            ProcessoDocumentoBin processoDocumentoBin) {
        this.processoDocumentoBin = processoDocumentoBin;
    }

    @Column(name = COL_NOME_PERFIL, nullable = false, length = NOME_PADRAO)
    @Size(max = NOME_PADRAO)
    @NotNull
    public String getNomePerfil() {
        return nomePerfil;
    }

    public void setNomePerfil(String nomePerfil) {
        this.nomePerfil = nomePerfil;
    }
    
    @Transient
    public String getNomePerfil() {
        AssinaturaDocumentoDAO add = ComponentUtil.getComponent(AssinaturaDocumentoDAO.NAME);
        return add.getNomePerfil(getNomeLocalizacao(), getNomePapel());
    }

}