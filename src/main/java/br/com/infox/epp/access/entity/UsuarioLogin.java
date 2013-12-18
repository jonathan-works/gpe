package br.com.infox.epp.access.entity;

import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PADRAO;
import static br.com.infox.core.constants.LengthConstants.FLAG;
import static br.com.infox.core.constants.LengthConstants.NOME_ATRIBUTO;
import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.core.persistence.ORConstants.PUBLIC;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.BLOQUEIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.DATA_EXPIRACAO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.EMAIL;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.ID_USUARIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.LOGIN;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.NOME_USUARIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.PROVISORIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.SENHA;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.SEQUENCE_USUARIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.TABLE_USUARIO_LOGIN;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.TIPO_USUARIO;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_BY_LOGIN_TASK_INSTANCE;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_BY_LOGIN_TASK_INSTANCE_QUERY;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.USUARIO_LOGIN_NAME;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.*;
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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
import org.jboss.seam.annotations.security.management.UserPassword;
import org.jboss.seam.annotations.security.management.UserPrincipal;
import org.jboss.seam.annotations.security.management.UserRoles;

import br.com.infox.epp.access.type.UsuarioEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.system.entity.EntityLog;

@Entity
@Table(name=TABLE_USUARIO_LOGIN, schema=PUBLIC , uniqueConstraints = @UniqueConstraint(columnNames = LOGIN))
@NamedQueries(value={
	@NamedQuery(name=USUARIO_LOGIN_NAME, query=USUARIO_LOGIN_QUERY),
	@NamedQuery(name=USUARIO_BY_LOGIN_TASK_INSTANCE, query=USUARIO_BY_LOGIN_TASK_INSTANCE_QUERY),
	@NamedQuery(name=USUARIO_BY_EMAIL, query=USUARIO_LOGIN_EMAIL_QUERY)
})
public class UsuarioLogin implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer idUsuarioLogin;
	private String email;
	private String senha;
	private String login;
	private String nomeUsuario;
	private Boolean ativo;
	private Boolean bloqueio;
	private Boolean provisorio;
	//Data de previsão para expirar o usuário provisório
	private Date dataExpiracao;
	private Boolean temContaTwitter;
	
	private UsuarioEnum tipoUsuario;
	private PessoaFisica pessoaFisica;

	private Set<Papel> papelSet = new TreeSet<Papel>();
	
	private List<ProcessoDocumentoBin> processoDocumentoBinList = new ArrayList<ProcessoDocumentoBin>(0);
	private List<Fluxo> fluxoList = new ArrayList<Fluxo>(0);
	private List<UsuarioLocalizacao> usuarioLocalizacaoList = new ArrayList<UsuarioLocalizacao>(0);
	private List<Processo> processoListForIdUsuarioCadastroProcesso = new ArrayList<Processo>(0);
	private List<BloqueioUsuario> bloqueioUsuarioList = new ArrayList<BloqueioUsuario>(0);
	private List<ProcessoDocumento> processoDocumentoListForIdUsuarioInclusao = new ArrayList<ProcessoDocumento>(0);
	private List<EntityLog> entityLogList = new ArrayList<EntityLog>(0);

	public UsuarioLogin() {
		dataExpiracao = null;
	}
	
	@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_USUARIO)
    @Id
    @GeneratedValue(generator=GENERATOR)
    @Column(name=ID_USUARIO, unique=true, nullable=false)
    public Integer getIdUsuarioLogin() {
        return idUsuarioLogin;
    }
    public void setIdUsuarioLogin(Integer idUsuarioLogin) {
        this.idUsuarioLogin = idUsuarioLogin;
    }
    
    @Column(name = EMAIL, length=DESCRICAO_PADRAO, unique = true, nullable = false)
    @Size(max=DESCRICAO_PADRAO)
    @NotNull
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

	@Column(name = SENHA, length=DESCRICAO_PADRAO)
	@Size(max=DESCRICAO_PADRAO)
	@UserPassword(hash = "SHA")
	public String getSenha() {
		return this.senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Column(name = LOGIN, unique = true, nullable = false, length=DESCRICAO_PADRAO)
	@Size(max=DESCRICAO_PADRAO)
	@NotNull
	@UserPrincipal
	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}
	
	@Column(name=NOME_USUARIO, nullable=false, length=NOME_ATRIBUTO)
    @Size(max=NOME_ATRIBUTO)
    public String getNomeUsuario() {
        return nomeUsuario;
    }
    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }
	
	@Column(name=ATIVO, nullable=false)
    public Boolean getAtivo() {
        return ativo;
    }
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Column(name=TIPO_USUARIO, length=FLAG, nullable=false)
    @Enumerated(EnumType.STRING)
    public UsuarioEnum getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(UsuarioEnum tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    @ManyToOne(fetch=LAZY)
    @JoinColumn(name="id_pessoa_fisica")
	public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    @UserRoles
	@ManyToMany
	@JoinTable(name = "tb_usuario_papel", schema=PUBLIC, joinColumns = @JoinColumn(name = "id_usuario"), inverseJoinColumns = @JoinColumn(name = "id_papel"))
	@ForeignKey(name="tb_usuario_papel_usuario_fk", inverseName = "tb_usuario_papel_papel_fk" )
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
		result = prime * result + ((getIdUsuarioLogin() == null) ? 0 : getIdUsuarioLogin().hashCode());
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
	@Column(name = DATA_EXPIRACAO, nullable=true)
	public Date getDataExpiracao() {
		return dataExpiracao;
	}

	public void setDataExpiracao(Date dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}

	@OneToMany(cascade = {PERSIST, MERGE, REFRESH}, fetch = LAZY, mappedBy = "usuario")
	public List<ProcessoDocumentoBin> getProcessoDocumentoBinList() {
		return this.processoDocumentoBinList;
	}

	public void setProcessoDocumentoBinList(
			List<ProcessoDocumentoBin> processoDocumentoBinList) {
		this.processoDocumentoBinList = processoDocumentoBinList;
	}

	@OneToMany(cascade = {PERSIST, MERGE, REFRESH}, fetch = LAZY, mappedBy = "usuarioPublicacao")
	public List<Fluxo> getFluxoList() {
		return this.fluxoList;
	}

	public void setFluxoList(List<Fluxo> fluxoList) {
		this.fluxoList = fluxoList;
	}
	
	@OneToMany(cascade = {PERSIST, MERGE, REFRESH}, fetch = LAZY, mappedBy = "usuario", orphanRemoval=true)
	@OrderBy("idUsuarioLocalizacao")
	public List<UsuarioLocalizacao> getUsuarioLocalizacaoList() {
		return this.usuarioLocalizacaoList;
	}

	public void setUsuarioLocalizacaoList(
			List<UsuarioLocalizacao> usuarioLocalizacaoList) {
		this.usuarioLocalizacaoList = usuarioLocalizacaoList;
	}
	
	@OneToMany(cascade = {PERSIST, MERGE, REFRESH}, fetch = LAZY, mappedBy = "usuarioCadastroProcesso")
	public List<Processo> getProcessoListForIdUsuarioCadastroProcesso() {
		return this.processoListForIdUsuarioCadastroProcesso;
	}

	public void setProcessoListForIdUsuarioCadastroProcesso(
			List<Processo> processoListForIdUsuarioCadastroProcesso) {
		this.processoListForIdUsuarioCadastroProcesso = processoListForIdUsuarioCadastroProcesso;
	}
	@OneToMany(cascade = {PERSIST, MERGE, REFRESH}, fetch = LAZY, mappedBy = "usuario")
	public List<BloqueioUsuario> getBloqueioUsuarioList() {
		return this.bloqueioUsuarioList;
	}

	public void setBloqueioUsuarioList(List<BloqueioUsuario> bloqueioUsuarioList) {
		this.bloqueioUsuarioList = bloqueioUsuarioList;
	}

	@OneToMany(cascade = {PERSIST, MERGE, REFRESH}, fetch = LAZY, mappedBy = "usuarioInclusao")
	public List<ProcessoDocumento> getProcessoDocumentoListForIdUsuarioInclusao() {
		return this.processoDocumentoListForIdUsuarioInclusao;
	}

	public void setProcessoDocumentoListForIdUsuarioInclusao(
			List<ProcessoDocumento> processoDocumentoListForIdUsuarioInclusao) {
		this.processoDocumentoListForIdUsuarioInclusao = processoDocumentoListForIdUsuarioInclusao;
	}
	
	@OneToMany(cascade = {PERSIST, MERGE, REFRESH}, fetch = LAZY, mappedBy = "usuario")	
	public List<EntityLog> getEntityLogList() {
		return entityLogList;
	}
	
	public void setEntityLogList(List<EntityLog> entityLogList) {
		this.entityLogList = entityLogList;
	}
	
	@Column(name="in_twitter", nullable=false)
	public Boolean getTemContaTwitter() {
		if (temContaTwitter == null) {
		    temContaTwitter = false;
		}
		return temContaTwitter;
	}

	public void setTemContaTwitter(Boolean temContaTwitter) {
		this.temContaTwitter = temContaTwitter;
	}
	
	@Override
	public String toString() {
	    return getNomeUsuario();
	}
	
	@Transient
	public boolean isHumano(){
	    return tipoUsuario == UsuarioEnum.H;
	}
	
}