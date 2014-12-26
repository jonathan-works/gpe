package br.com.infox.epp.access.entity;

import static br.com.infox.constants.LengthConstants.DESCRICAO_PADRAO;
import static br.com.infox.constants.LengthConstants.FLAG;
import static br.com.infox.constants.LengthConstants.NOME_ATRIBUTO;
import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.ACTORID_TAREFA_ATUAL_BY_PROCESSO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.ACTORID_TAREFA_ATUAL_BY_PROCESSO_QUERY;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.BLOQUEIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.DATA_EXPIRACAO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.EMAIL;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.ID_USUARIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.INATIVAR_USUARIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.INATIVAR_USUARIO_QUERY;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.LOGIN;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.NOME_USUARIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.NOME_USUARIO_BY_ID_TASK_INSTANCE;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.NOME_USUARIO_BY_ID_TASK_INSTANCE_QUERY;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.PROVISORIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.SENHA;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.SEQUENCE_USUARIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.TABLE_USUARIO_LOGIN;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.TIPO_USUARIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_BY_EMAIL;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_BY_ID_TASK_INSTANCE;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_BY_ID_TASK_INSTANCE_QUERY;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_BY_LOGIN_TASK_INSTANCE;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_BY_LOGIN_TASK_INSTANCE_QUERY;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_BY_PESSOA;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_BY_PESSOA_QUERY;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_FETCH_PF_BY_NUMERO_CPF;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_FETCH_PF_BY_NUMERO_CPF_QUERY;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_LOGIN_EMAIL_QUERY;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_LOGIN_NAME;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_LOGIN_QUERY;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ForeignKey;
import org.jboss.seam.annotations.security.management.PasswordSalt;
import org.jboss.seam.annotations.security.management.UserPassword;
import org.jboss.seam.annotations.security.management.UserPrincipal;
import org.jboss.seam.annotations.security.management.UserRoles;

import br.com.infox.epp.access.type.UsuarioEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.system.entity.EntityLog;

@Entity
@Table(name = TABLE_USUARIO_LOGIN, uniqueConstraints = {
        @UniqueConstraint(columnNames = LOGIN),
        @UniqueConstraint(columnNames = "id_pessoa_fisica") })
@NamedQueries(value = {
        @NamedQuery(name = USUARIO_LOGIN_NAME, query = USUARIO_LOGIN_QUERY),
        @NamedQuery(name = USUARIO_BY_LOGIN_TASK_INSTANCE, query = USUARIO_BY_LOGIN_TASK_INSTANCE_QUERY),
        @NamedQuery(name = USUARIO_BY_EMAIL, query = USUARIO_LOGIN_EMAIL_QUERY),
        @NamedQuery(name = INATIVAR_USUARIO, query = INATIVAR_USUARIO_QUERY),
        @NamedQuery(name = USUARIO_BY_PESSOA, query = USUARIO_BY_PESSOA_QUERY),
        @NamedQuery(name = USUARIO_FETCH_PF_BY_NUMERO_CPF, query = USUARIO_FETCH_PF_BY_NUMERO_CPF_QUERY) })
@NamedNativeQueries({
        @NamedNativeQuery(name = USUARIO_BY_ID_TASK_INSTANCE, query = USUARIO_BY_ID_TASK_INSTANCE_QUERY),
        @NamedNativeQuery(name = ACTORID_TAREFA_ATUAL_BY_PROCESSO, query = ACTORID_TAREFA_ATUAL_BY_PROCESSO_QUERY),
        @NamedNativeQuery(name = NOME_USUARIO_BY_ID_TASK_INSTANCE, query = NOME_USUARIO_BY_ID_TASK_INSTANCE_QUERY) })
public class UsuarioLogin implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idUsuarioLogin;
    private String email;
    private String senha;
    private String login;
    private String salt;
    private String nomeUsuario;
    private Boolean ativo;
    private Boolean bloqueio;
    private Boolean provisorio;
    // Data de previsão para expirar o usuário provisório
    private Date dataExpiracao;
    private Boolean temContaTwitter;

    private UsuarioEnum tipoUsuario;
    private PessoaFisica pessoaFisica;

    private Set<Papel> papelSet;

    private List<Fluxo> fluxoList;
    private List<UsuarioPerfil> usuarioPerfilList;
    private List<Processo> processoListForIdUsuarioCadastroProcesso;
    private List<BloqueioUsuario> bloqueioUsuarioList;
    private List<Documento> processoDocumentoListForIdUsuarioInclusao;
    private List<EntityLog> entityLogList;

    public UsuarioLogin() {
        papelSet = new TreeSet<>();
        fluxoList = new ArrayList<>(0);
        usuarioPerfilList = new ArrayList<>(0);
        processoListForIdUsuarioCadastroProcesso = new ArrayList<>(0);
        bloqueioUsuarioList = new ArrayList<>(0);
        processoDocumentoListForIdUsuarioInclusao = new ArrayList<>(0);
        entityLogList = new ArrayList<>(0);
    }

    private UsuarioLogin(final String nomeUsuario, final String email,
            final String login, UsuarioEnum tipoUsuario, Boolean ativo,
            Boolean provisorio, Boolean bloqueio) {
        this();
        this.nomeUsuario = nomeUsuario;
        this.email = email;
        this.login = login;
        this.tipoUsuario = tipoUsuario;
        this.ativo = ativo;
        this.provisorio = provisorio;
        this.bloqueio = bloqueio;
    }

    public UsuarioLogin(final String nomeUsuario, final String email,
            final String login, final UsuarioEnum tipoUsuario,
            final Boolean ativo) {
        this(nomeUsuario, email, login, tipoUsuario, ativo, Boolean.FALSE,
                Boolean.FALSE);
    }

    public UsuarioLogin(final String nomeUsuario, final String email,
            final String login) {
        this(nomeUsuario, email, login, UsuarioEnum.H, Boolean.TRUE);
    }

    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR, sequenceName = SEQUENCE_USUARIO)
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_USUARIO, unique = true, nullable = false)
    public Integer getIdUsuarioLogin() {
        return idUsuarioLogin;
    }

    public void setIdUsuarioLogin(Integer idUsuarioLogin) {
        this.idUsuarioLogin = idUsuarioLogin;
    }

    @Column(name = EMAIL, length = DESCRICAO_PADRAO, unique = true, nullable = false)
    @Size(max = DESCRICAO_PADRAO)
    @NotNull
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = SENHA, length = DESCRICAO_PADRAO)
    @Size(max = DESCRICAO_PADRAO)
    @UserPassword(hash = "SHA")
    public String getSenha() {
        return this.senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Column(name = LOGIN, unique = true, nullable = false, length = DESCRICAO_PADRAO)
    @Size(max = DESCRICAO_PADRAO)
    @NotNull
    @UserPrincipal
    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Column(name = NOME_USUARIO, nullable = false, length = NOME_ATRIBUTO)
    @Size(max = NOME_ATRIBUTO)
    @NotNull
    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    @Column(name = ATIVO, nullable = false)
    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Column(name = TIPO_USUARIO, length = FLAG, nullable = false)
    @Enumerated(EnumType.STRING)
    public UsuarioEnum getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(UsuarioEnum tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "id_pessoa_fisica")
    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    @UserRoles
    @ManyToMany
    @JoinTable(name = "tb_usuario_papel", joinColumns = @JoinColumn(name = "id_usuario"), inverseJoinColumns = @JoinColumn(name = "id_papel"))
    @ForeignKey(name = "tb_usuario_papel_usuario_fk", inverseName = "tb_usuario_papel_papel_fk")
    public Set<Papel> getPapelSet() {
        return this.papelSet;
    }

    public void setPapelSet(Set<Papel> papelSet) {
        this.papelSet = papelSet;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getIdUsuarioLogin() == null) {
            return false;
        }
        if (!(obj instanceof UsuarioLogin)) {
            return false;
        }
        UsuarioLogin other = (UsuarioLogin) obj;
        return getIdUsuarioLogin().equals(other.getIdUsuarioLogin());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((getIdUsuarioLogin() == null) ? 0 : getIdUsuarioLogin()
                        .hashCode());
        return result;
    }

    @Column(name = BLOQUEIO, nullable = false)
    @NotNull
    public Boolean getBloqueio() {
        return this.bloqueio;
    }

    public void setBloqueio(Boolean bloqueio) {
        this.bloqueio = bloqueio;
    }

    @Column(name = PROVISORIO)
    public Boolean getProvisorio() {
        return this.provisorio;
    }

    public void setProvisorio(Boolean provisorio) {
        this.provisorio = provisorio;
    }

    @Temporal(TIMESTAMP)
    @Column(name = DATA_EXPIRACAO, nullable = true)
    public Date getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(Date dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    @OneToMany(cascade = { PERSIST, MERGE, REFRESH }, fetch = LAZY, mappedBy = "usuarioPublicacao")
    public List<Fluxo> getFluxoList() {
        return this.fluxoList;
    }

    public void setFluxoList(List<Fluxo> fluxoList) {
        this.fluxoList = fluxoList;
    }

    @OneToMany(fetch = LAZY, mappedBy = "usuarioLogin", orphanRemoval = true)
    @OrderBy("idUsuarioPerfil")
    public List<UsuarioPerfil> getUsuarioPerfilList() {
        return this.usuarioPerfilList;
    }

    public void setUsuarioPerfilList(List<UsuarioPerfil> usuarioPerfilList) {
        this.usuarioPerfilList = usuarioPerfilList;
    }

    @OneToMany(cascade = { PERSIST, MERGE, REFRESH }, fetch = LAZY, mappedBy = "usuarioCadastro")
    public List<Processo> getProcessoListForIdUsuarioCadastroProcesso() {
        return this.processoListForIdUsuarioCadastroProcesso;
    }

    public void setProcessoListForIdUsuarioCadastroProcesso(
            List<Processo> processoListForIdUsuarioCadastroProcesso) {
        this.processoListForIdUsuarioCadastroProcesso = processoListForIdUsuarioCadastroProcesso;
    }

    @OneToMany(cascade = { PERSIST, MERGE, REFRESH }, fetch = LAZY, mappedBy = "usuario")
    public List<BloqueioUsuario> getBloqueioUsuarioList() {
        return this.bloqueioUsuarioList;
    }

    public void setBloqueioUsuarioList(List<BloqueioUsuario> bloqueioUsuarioList) {
        this.bloqueioUsuarioList = bloqueioUsuarioList;
    }

    @OneToMany(cascade = { PERSIST, MERGE, REFRESH }, fetch = LAZY, mappedBy = "usuarioInclusao")
    public List<Documento> getProcessoDocumentoListForIdUsuarioInclusao() {
        return this.processoDocumentoListForIdUsuarioInclusao;
    }

    public void setProcessoDocumentoListForIdUsuarioInclusao(
            List<Documento> processoDocumentoListForIdUsuarioInclusao) {
        this.processoDocumentoListForIdUsuarioInclusao = processoDocumentoListForIdUsuarioInclusao;
    }

    @OneToMany(cascade = { PERSIST, MERGE, REFRESH }, fetch = LAZY, mappedBy = "usuario")
    public List<EntityLog> getEntityLogList() {
        return entityLogList;
    }

    public void setEntityLogList(List<EntityLog> entityLogList) {
        this.entityLogList = entityLogList;
    }

    @Column(name = "in_twitter", nullable = false)
    public Boolean getTemContaTwitter() {
        if (temContaTwitter == null) {
            temContaTwitter = false;
        }
        return temContaTwitter;
    }

    public void setTemContaTwitter(Boolean temContaTwitter) {
        this.temContaTwitter = temContaTwitter;
    }

    @Column(name = "ds_salt", length = 16, nullable = false)
    @PasswordSalt
    @NotNull
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        return getNomeUsuario();
    }

    @Transient
    public boolean isHumano() {
        return UsuarioEnum.H.equals(tipoUsuario);
    }

    @Transient
    public List<UsuarioPerfil> getUsuarioPerfilAtivoList() {
        List<UsuarioPerfil> result = new ArrayList<>();
        for (UsuarioPerfil usuarioPerfil : getUsuarioPerfilList()) {
            if (usuarioPerfil.getPerfilTemplate().getAtivo()) {
                result.add(usuarioPerfil);
            }
        }
        return result;
    }

    @Transient
    public String getPerfisFormatados() {
        if (getUsuarioPerfilList().isEmpty()) {
            return "";
        } else {
            return getUsuarioPerfilList().toString();
        }

    }

}
