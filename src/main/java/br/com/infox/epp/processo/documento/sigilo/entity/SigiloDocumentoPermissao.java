package br.com.infox.epp.processo.documento.sigilo.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoPermissaoQuery.COLUMN_ATIVO;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoPermissaoQuery.COLUMN_ID;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoPermissaoQuery.COLUMN_ID_SIGILO_DOCUMENTO;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoPermissaoQuery.COLUMN_ID_USUARIO_LOGIN;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoPermissaoQuery.NAMED_QUERY_DOCUMENTOS_PERMITIDOS;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoPermissaoQuery.NAMED_QUERY_INATIVAR_PERMISSOES;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoPermissaoQuery.NAMED_QUERY_PERMISSOES_DO_SIGILO;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoPermissaoQuery.NAMED_QUERY_USUARIO_POSSUI_PERMISSAO;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoPermissaoQuery.NAMED_QUERY_USUARIO_POSSUI_PERMISSAO_DOCUMENTOS;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoPermissaoQuery.QUERY_DOCUMENTOS_PERMITIDOS;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoPermissaoQuery.QUERY_INATIVAR_PERMISSOES;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoPermissaoQuery.QUERY_PERMISSOES_DO_SIGILO;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoPermissaoQuery.QUERY_USUARIO_POSSUI_PERMISSAO;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoPermissaoQuery.QUERY_USUARIO_POSSUI_PERMISSAO_DOCUMENTOS;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoPermissaoQuery.SEQUENCE_NAME;
import static br.com.infox.epp.processo.documento.sigilo.query.SigiloDocumentoPermissaoQuery.TABLE_NAME;

import java.io.Serializable;

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

import br.com.infox.epp.access.entity.UsuarioLogin;

@Entity
@Table(name = TABLE_NAME)
@NamedQueries({
    @NamedQuery(name = NAMED_QUERY_USUARIO_POSSUI_PERMISSAO, query = QUERY_USUARIO_POSSUI_PERMISSAO),
    @NamedQuery(name = NAMED_QUERY_INATIVAR_PERMISSOES, query = QUERY_INATIVAR_PERMISSOES),
    @NamedQuery(name = NAMED_QUERY_PERMISSOES_DO_SIGILO, query = QUERY_PERMISSOES_DO_SIGILO),
    @NamedQuery(name = NAMED_QUERY_USUARIO_POSSUI_PERMISSAO_DOCUMENTOS, query = QUERY_USUARIO_POSSUI_PERMISSAO_DOCUMENTOS),
    @NamedQuery(name = NAMED_QUERY_DOCUMENTOS_PERMITIDOS, query = QUERY_DOCUMENTOS_PERMITIDOS) })
public class SigiloDocumentoPermissao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_NAME)
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = COLUMN_ID)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_ID_SIGILO_DOCUMENTO, nullable = false)
    private SigiloDocumento sigiloDocumento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_ID_USUARIO_LOGIN, nullable = false)
    private UsuarioLogin usuario;

    @Column(name = COLUMN_ATIVO, nullable = false)
    private Boolean ativo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SigiloDocumento getSigiloDocumento() {
        return sigiloDocumento;
    }

    public void setSigiloDocumento(SigiloDocumento sigiloDocumento) {
        this.sigiloDocumento = sigiloDocumento;
    }

    public UsuarioLogin getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioLogin usuario) {
        this.usuario = usuario;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
