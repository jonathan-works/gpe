package br.com.infox.epp.access.entity;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;
import static javax.persistence.TemporalType.*;

import static br.com.infox.core.constants.LengthConstants.*;
import static br.com.infox.epp.access.query.UsuarioLoginQuery.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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

import org.hibernate.annotations.ForeignKey;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import org.jboss.seam.annotations.security.management.UserPassword;
import org.jboss.seam.annotations.security.management.UserPrincipal;
import org.jboss.seam.annotations.security.management.UserRoles;

import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.system.entity.EntityLog;
import br.com.itx.util.StringUtil;

@Entity
@Table(name=UsuarioLogin.TABLE_NAME, schema="public" , uniqueConstraints = @UniqueConstraint(columnNames = "ds_login"))
@NamedQueries(value={
	@NamedQuery(name=USUARIO_LOGIN_NAME, query=USUARIO_LOGIN_QUERY),
	@NamedQuery(name=USUARIO_BY_LOGIN_TASK_INSTANCE, query=USUARIO_BY_LOGIN_TASK_INSTANCE_QUERY)
})
public class UsuarioLogin implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_usuario_login"; 

	private Integer idUsuarioLogin;
	private String email;
	private String senha;
	private String login;
	private String nomeUsuario;
	private Boolean ativo;
	private String assinatura;
	private String certChain;
	private Boolean bloqueio;
	private Boolean provisorio;
	//Data de previsão para expirar o usuário provisório
	private Date dataExpiracao;
	private Boolean temContaTwitter;
	
	private PessoaFisica pessoaFisica;

	private Set<Papel> papelSet = new TreeSet<Papel>();
	
	private List<ProcessoDocumentoBin> processoDocumentoBinList = new ArrayList<ProcessoDocumentoBin>(0);
	private List<Fluxo> fluxoList = new ArrayList<Fluxo>(0);
	private List<UsuarioLocalizacao> usuarioLocalizacaoList = new ArrayList<UsuarioLocalizacao>(0);
	private List<Processo> processoListForIdUsuarioCadastroProcesso = new ArrayList<Processo>(0);
	private List<BloqueioUsuario> bloqueioUsuarioList = new ArrayList<BloqueioUsuario>(0);
	private List<ProcessoDocumento> processoDocumentoListForIdUsuarioInclusao = new ArrayList<ProcessoDocumento>(0);
	private List<ProcessoDocumento> processoDocumentoListForIdUsuarioExclusao = new ArrayList<ProcessoDocumento>(0);
	private List<EntityLog> entityLogList = new ArrayList<EntityLog>(0);

	public UsuarioLogin() {
		dataExpiracao = null;
	}
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_pessoa")
    @Id
    @GeneratedValue(generator="generator")
    @Column(name="id_usuario_login", unique=true, nullable=false)
    public Integer getIdUsuarioLogin() {
        return idUsuarioLogin;
    }
    public void setIdUsuarioLogin(Integer idUsuarioLogin) {
        this.idUsuarioLogin = idUsuarioLogin;
    }
    
    @Column(name = "ds_email", length=DESCRICAO_PADRAO, unique = true, nullable = false)
    @Size(max=DESCRICAO_PADRAO)
    @NotNull
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

	@Column(name = "ds_senha", length=DESCRICAO_PADRAO)
	@Size(max=DESCRICAO_PADRAO)
	@UserPassword(hash = "SHA")
	public String getSenha() {
		return this.senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Column(name = "ds_login", unique = true, nullable = false, length=DESCRICAO_PADRAO)
	@Size(max=DESCRICAO_PADRAO)
	@NotNull
	@UserPrincipal
	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}
	
	@Column(name="nm_usuario", nullable=false, length=NOME_ATRIBUTO)
    @Size(max=NOME_ATRIBUTO)
    public String getNomeUsuario() {
        return nomeUsuario;
    }
    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }
	
	@Column(name = "ds_assinatura_usuario")
	public String getAssinatura() {
		return assinatura;
	}
	
	public void setAssinatura(String assinatura) {
		this.assinatura = assinatura;
	}
	
	@Column(name = "ds_cert_chain_usuario")
	@Basic(fetch = LAZY)
	public String getCertChain() {
		return certChain;
	}
	
	public void setCertChain(String certChain) {
		this.certChain = certChain;
	}
	
	@Column(name="in_ativo", nullable=false)
    public Boolean getAtivo() {
        return ativo;
    }
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
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
	@JoinTable(name = "tb_usuario_papel", schema="public", joinColumns = @JoinColumn(name = "id_usuario"), inverseJoinColumns = @JoinColumn(name = "id_papel"))
	@ForeignKey(name="tb_usuario_papel_usuario_fk", inverseName = "tb_usuario_papel_papel_fk" )
	public Set<Papel> getPapelSet() {
		return this.papelSet;
	}
	
	public void setPapelSet(Set<Papel> papelSet) {
		this.papelSet = papelSet;
	}
	
	@Transient
	public boolean checkCertChain(String certChain) {
		if (certChain == null) {
			throw new IllegalArgumentException("O parâmetro não deve ser nulo");
		} 
		return StringUtil.replaceQuebraLinha(certChain).equals(
				StringUtil.replaceQuebraLinha(this.certChain));
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

	@Column(name = "in_bloqueio", nullable = false)
	@NotNull
	public Boolean getBloqueio() {
		return this.bloqueio;
	}

	public void setBloqueio(Boolean bloqueio) {
		this.bloqueio = bloqueio;
	}

	@Column(name = "in_provisorio")
	public Boolean getProvisorio() {
		return this.provisorio;
	}

	public void setProvisorio(Boolean provisorio) {
		this.provisorio = provisorio;
	}
	
	@Temporal(TIMESTAMP)
	@Column(name = "dt_expiracao_usuario", nullable=true)
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
	
	@OneToMany(cascade = {PERSIST, MERGE, REFRESH}, fetch = LAZY, mappedBy = "usuarioExclusao")
	public List<ProcessoDocumento> getProcessoDocumentoListForIdUsuarioExclusao() {
		return this.processoDocumentoListForIdUsuarioExclusao;
	}

	public void setProcessoDocumentoListForIdUsuarioExclusao(
			List<ProcessoDocumento> processoDocumentoListForIdUsuarioExclusao) {
		this.processoDocumentoListForIdUsuarioExclusao = processoDocumentoListForIdUsuarioExclusao;
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
	
	@Transient
	public BloqueioUsuario getUltimoBloqueio(){
	    if (!bloqueioUsuarioList.isEmpty()){
	        return bloqueioUsuarioList.get(bloqueioUsuarioList.size() - 1);
	    } else {
	        return null;
	    }
	}
	
	public boolean permaneceBloqueado(){
	    return bloqueio && getUltimoBloqueio().getDataDesbloqueio() != null;
	}
	
	@Override
	public String toString() {
	    return getNomeUsuario();
	}
	
}