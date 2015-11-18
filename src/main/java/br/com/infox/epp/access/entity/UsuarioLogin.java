package br.com.infox.epp.access.entity;

import static br.com.infox.constants.LengthConstants.DESCRICAO_PADRAO;
import static br.com.infox.constants.LengthConstants.FLAG;
import static br.com.infox.constants.LengthConstants.NOME_ATRIBUTO;
import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.BLOQUEIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.DATA_EXPIRACAO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.EMAIL;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.ID_USUARIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.INATIVAR_USUARIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.INATIVAR_USUARIO_QUERY;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.LOGIN;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.NOME_USUARIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.NOME_USUARIO_BY_ID_TAREFA;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.NOME_USUARIO_BY_ID_TAREFA_QUERY;
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
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_LOGIN_LOCALIZACAO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_LOGIN_LOCALIZACAO_PAPEL;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_LOGIN_LOCALIZACAO_PAPEL_QUERY;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_LOGIN_LOCALIZACAO_QUERY;
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
import javax.persistence.PrePersist;
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

import br.com.infox.constants.LengthConstants;
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
        @NamedQuery(name = USUARIO_LOGIN_LOCALIZACAO_PAPEL, query = USUARIO_LOGIN_LOCALIZACAO_PAPEL_QUERY),
        @NamedQuery(name = USUARIO_LOGIN_LOCALIZACAO, query = USUARIO_LOGIN_LOCALIZACAO_QUERY),
        @NamedQuery(name = USUARIO_FETCH_PF_BY_NUMERO_CPF, query = USUARIO_FETCH_PF_BY_NUMERO_CPF_QUERY) })
@NamedNativeQueries({
    @NamedNativeQuery(name = USUARIO_BY_ID_TASK_INSTANCE, query = USUARIO_BY_ID_TASK_INSTANCE_QUERY),
    @NamedNativeQuery(name = NOME_USUARIO_BY_ID_TAREFA, query = NOME_USUARIO_BY_ID_TAREFA_QUERY),
    @NamedNativeQuery(name = NOME_USUARIO_BY_ID_TASK_INSTANCE, query = NOME_USUARIO_BY_ID_TASK_INSTANCE_QUERY) })
public class UsuarioLogin implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR, sequenceName = SEQUENCE_USUARIO)
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_USUARIO, unique = true, nullable = false)
    private Integer idUsuarioLogin;

    @NotNull
    @Column(name = EMAIL, length = DESCRICAO_PADRAO, unique = true, nullable = false)
    @Size(min = LengthConstants.FLAG, max = DESCRICAO_PADRAO)
    private String email;

    @Column(name = SENHA, length = DESCRICAO_PADRAO)
    @Size(max = DESCRICAO_PADRAO)
    @UserPassword(hash = "SHA")
    private String senha;

    @UserPrincipal
    @NotNull
    @Column(name = LOGIN, unique = true, nullable = false, length = DESCRICAO_PADRAO)
    @Size(min = LengthConstants.FLAG, max = DESCRICAO_PADRAO)
    private String login;

    @PasswordSalt
    @NotNull
    @Column(name = "ds_salt", length = 16, nullable = false)
    private String salt;

    @NotNull
    @Size(min = LengthConstants.FLAG, max = NOME_ATRIBUTO)
    @Column(name = NOME_USUARIO, nullable = false, length = NOME_ATRIBUTO)
    private String nomeUsuario;

    @Column(name = ATIVO, nullable = false)
    private Boolean ativo;

    @NotNull
    @Column(name = BLOQUEIO, nullable = false)
    private Boolean bloqueio;

    @Column(name = PROVISORIO)
    private Boolean provisorio;

    @Temporal(TIMESTAMP)
    @Column(name = DATA_EXPIRACAO, nullable = true)
    private Date dataExpiracao;

    @Column(name = "in_twitter", nullable = false)
    private Boolean temContaTwitter;

    @Enumerated(EnumType.STRING)
    @Column(name = TIPO_USUARIO, length = FLAG, nullable = false)
    private UsuarioEnum tipoUsuario;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "id_pessoa_fisica")
    private PessoaFisica pessoaFisica;

    @UserRoles
    @ManyToMany
    @JoinTable(name = "tb_usuario_papel", joinColumns = @JoinColumn(name = "id_usuario"), inverseJoinColumns = @JoinColumn(name = "id_papel"))
    @ForeignKey(name = "tb_usuario_papel_usuario_fk", inverseName = "tb_usuario_papel_papel_fk")
    private Set<Papel> papelSet;

    @OneToMany(cascade = { PERSIST, MERGE, REFRESH }, fetch = LAZY, mappedBy = "usuarioPublicacao")
    private List<Fluxo> fluxoList;

    @OneToMany(fetch = LAZY, mappedBy = "usuarioLogin", orphanRemoval = true)
    @OrderBy("idUsuarioPerfil")
    private List<UsuarioPerfil> usuarioPerfilList;

    @OneToMany(cascade = { PERSIST, MERGE, REFRESH }, fetch = LAZY, mappedBy = "usuarioCadastro")
    private List<Processo> processoListForIdUsuarioCadastroProcesso;

    @OneToMany(cascade = { PERSIST, MERGE, REFRESH }, fetch = LAZY, mappedBy = "usuario")
    private List<BloqueioUsuario> bloqueioUsuarioList;

    @OneToMany(cascade = { PERSIST, MERGE, REFRESH }, fetch = LAZY, mappedBy = "usuarioInclusao")
    private List<Documento> processoDocumentoListForIdUsuarioInclusao;

    @OneToMany(cascade = { PERSIST, MERGE, REFRESH }, fetch = LAZY, mappedBy = "usuario")
    private List<EntityLog> entityLogList;

    @PrePersist
    private void prePersist() {
        if (provisorio == null) {
            setProvisorio(false);
        }
        if (bloqueio == null) {
            setBloqueio(false);
        }
        if (temContaTwitter == null) {
            setTemContaTwitter(false);
        }
    }

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

    public UsuarioLogin(String nomeUsuario, String email, String login, UsuarioEnum tipoUsuario, Boolean ativo) {
        this(nomeUsuario, email, login, tipoUsuario, ativo, Boolean.FALSE, Boolean.FALSE);
    }

    public UsuarioLogin(final String nomeUsuario, final String email, final String login) {
        this(nomeUsuario, email, login, UsuarioEnum.H, Boolean.TRUE);
    }

    public Integer getIdUsuarioLogin() {
        return idUsuarioLogin;
    }

    public void setIdUsuarioLogin(Integer idUsuarioLogin) {
        this.idUsuarioLogin = idUsuarioLogin;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return this.senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public UsuarioEnum getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(UsuarioEnum tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public Set<Papel> getPapelSet() {
        return this.papelSet;
    }

    public void setPapelSet(Set<Papel> papelSet) {
        this.papelSet = papelSet;
    }

    public Boolean getBloqueio() {
        return this.bloqueio;
    }

    public void setBloqueio(Boolean bloqueio) {
        this.bloqueio = bloqueio;
    }

    public Boolean getProvisorio() {
        return this.provisorio;
    }

    public void setProvisorio(Boolean provisorio) {
        this.provisorio = provisorio;
    }

    public Date getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(Date dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    public List<Fluxo> getFluxoList() {
        return this.fluxoList;
    }

    public void setFluxoList(List<Fluxo> fluxoList) {
        this.fluxoList = fluxoList;
    }

    public List<UsuarioPerfil> getUsuarioPerfilList() {
        return this.usuarioPerfilList;
    }

    public void setUsuarioPerfilList(List<UsuarioPerfil> usuarioPerfilList) {
        this.usuarioPerfilList = usuarioPerfilList;
    }

    public List<Processo> getProcessoListForIdUsuarioCadastroProcesso() {
        return this.processoListForIdUsuarioCadastroProcesso;
    }

    public void setProcessoListForIdUsuarioCadastroProcesso(
            List<Processo> processoListForIdUsuarioCadastroProcesso) {
        this.processoListForIdUsuarioCadastroProcesso = processoListForIdUsuarioCadastroProcesso;
    }

    public List<BloqueioUsuario> getBloqueioUsuarioList() {
        return this.bloqueioUsuarioList;
    }

    public void setBloqueioUsuarioList(List<BloqueioUsuario> bloqueioUsuarioList) {
        this.bloqueioUsuarioList = bloqueioUsuarioList;
    }

    public List<Documento> getProcessoDocumentoListForIdUsuarioInclusao() {
        return this.processoDocumentoListForIdUsuarioInclusao;
    }

    public void setProcessoDocumentoListForIdUsuarioInclusao(
            List<Documento> processoDocumentoListForIdUsuarioInclusao) {
        this.processoDocumentoListForIdUsuarioInclusao = processoDocumentoListForIdUsuarioInclusao;
    }

    public List<EntityLog> getEntityLogList() {
        return entityLogList;
    }

    public void setEntityLogList(List<EntityLog> entityLogList) {
        this.entityLogList = entityLogList;
    }

    public Boolean getTemContaTwitter() {
        if (temContaTwitter == null) {
            temContaTwitter = false;
        }
        return temContaTwitter;
    }

    public void setTemContaTwitter(Boolean temContaTwitter) {
        this.temContaTwitter = temContaTwitter;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getIdUsuarioLogin() == null) ? 0 : getIdUsuarioLogin().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UsuarioLogin))
			return false;
		UsuarioLogin other = (UsuarioLogin) obj;
		if (getIdUsuarioLogin() == null) {
			if (other.getIdUsuarioLogin() != null)
				return false;
		} else if (!getIdUsuarioLogin().equals(other.getIdUsuarioLogin()))
			return false;
		return true;
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
    public String getPerfisFormatados() {
    	StringBuilder sb = new StringBuilder();
    	List<UsuarioPerfil> perfis = getUsuarioPerfilList();
    	if (!perfis.isEmpty()) {
	    	for (UsuarioPerfil perfil : perfis) {
	    		if (perfil.getAtivo()){
		    		sb.append(perfil.toString());
		    		sb.append(", ");
	    		}
	    	}
	    }
    	if (sb.length() > 0) {
    		sb.delete(sb.length() - 2, sb.length());
    	}
    	return sb.toString();
    }

}
