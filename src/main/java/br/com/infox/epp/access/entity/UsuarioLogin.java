package br.com.infox.epp.access.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import org.hibernate.annotations.ForeignKey;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.security.management.UserPassword;
import org.jboss.seam.annotations.security.management.UserPrincipal;
import org.jboss.seam.annotations.security.management.UserRoles;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.access.query.UsuarioLoginQuery;
import br.com.infox.epp.endereco.entity.Endereco;
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
	@NamedQuery(name=UsuarioLoginQuery.USUARIO_LOGIN_NAME, query=UsuarioLoginQuery.USUARIO_LOGIN_QUERY),
	@NamedQuery(name=UsuarioLoginQuery.USUARIO_BY_LOGIN_TASK_INSTANCE, query=UsuarioLoginQuery.USUARIO_BY_LOGIN_TASK_INSTANCE_QUERY)
})
@Inheritance(strategy=InheritanceType.JOINED)
@BypassInterceptors
@PrimaryKeyJoinColumn(name="id_pessoa", columnDefinition = "integer")
public class UsuarioLogin extends PessoaFisica implements UsuarioLoginQuery, Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_usuario_login"; 

	private String senha;
	private String login;
	private String assinatura;
	private String certChain;
	private Boolean ldap = false;
	private Boolean bloqueio;
	private Boolean provisorio;
	//Data de previsão para expirar o usuário provisório
	private Date dataExpiracao;
	private Boolean temContaTwitter;

	private Set<Papel> papelSet = new TreeSet<Papel>();
	
	private List<ProcessoDocumentoBin> processoDocumentoBinList = new ArrayList<ProcessoDocumentoBin>(0);
	private List<Fluxo> fluxoList = new ArrayList<Fluxo>(0);
	private List<UsuarioLocalizacao> usuarioLocalizacaoList = new ArrayList<UsuarioLocalizacao>(0);
	private List<Processo> processoListForIdUsuarioCadastroProcesso = new ArrayList<Processo>(0);
	private List<BloqueioUsuario> bloqueioUsuarioList = new ArrayList<BloqueioUsuario>(0);
	private List<Endereco> enderecoList = new ArrayList<Endereco>(0);
	private List<ProcessoDocumento> processoDocumentoListForIdUsuarioInclusao = new ArrayList<ProcessoDocumento>(0);
	private List<ProcessoDocumento> processoDocumentoListForIdUsuarioExclusao = new ArrayList<ProcessoDocumento>(0);
	private List<EntityLog> entityLogList = new ArrayList<EntityLog>(0);


	public UsuarioLogin() {
		dataExpiracao = null;
	}

	@Column(name = "ds_senha", length=LengthConstants.DESCRICAO_PADRAO)
	@Size(max=LengthConstants.DESCRICAO_PADRAO)
	@UserPassword(hash = "SHA")
	public String getSenha() {
		return this.senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Column(name = "ds_login", unique = true, nullable = false, length=LengthConstants.DESCRICAO_PADRAO)
	@Size(max=LengthConstants.DESCRICAO_PADRAO)
	@NotNull
	@UserPrincipal
	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}
	
	@Column(name = "ds_assinatura_usuario")
	public String getAssinatura() {
		return assinatura;
	}
	
	public void setAssinatura(String assinatura) {
		this.assinatura = assinatura;
	}
	
	@Column(name = "ds_cert_chain_usuario")
	@Basic(fetch = FetchType.LAZY)
	public String getCertChain() {
		return certChain;
	}
	
	public void setCertChain(String certChain) {
		this.certChain = certChain;
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
		if (getIdPessoa() == null) {
			return false;
		}
		if (!(obj instanceof UsuarioLogin)) {
			return false;
		}
		UsuarioLogin other = (UsuarioLogin) obj;
		return getIdPessoa().equals(other.getIdPessoa());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getIdPessoa() == null) ? 0 : getIdPessoa().hashCode());
		return result;
	}

	@Column(name = "in_ldap")
	@NotNull
	public Boolean getLdap() {
		return ldap;
	}

	public void setLdap(Boolean ldap) {
		this.ldap = ldap;
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
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_expiracao_usuario", nullable=true)
	public Date getDataExpiracao() {
		return dataExpiracao;
	}

	public void setDataExpiracao(Date dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "usuario")
	public List<ProcessoDocumentoBin> getProcessoDocumentoBinList() {
		return this.processoDocumentoBinList;
	}

	public void setProcessoDocumentoBinList(
			List<ProcessoDocumentoBin> processoDocumentoBinList) {
		this.processoDocumentoBinList = processoDocumentoBinList;
	}

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "usuarioPublicacao")
	public List<Fluxo> getFluxoList() {
		return this.fluxoList;
	}

	public void setFluxoList(List<Fluxo> fluxoList) {
		this.fluxoList = fluxoList;
	}
	
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "usuario", orphanRemoval=true)
	@OrderBy("idUsuarioLocalizacao")
	public List<UsuarioLocalizacao> getUsuarioLocalizacaoList() {
		return this.usuarioLocalizacaoList;
	}

	public void setUsuarioLocalizacaoList(
			List<UsuarioLocalizacao> usuarioLocalizacaoList) {
		this.usuarioLocalizacaoList = usuarioLocalizacaoList;
	}
	
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "usuarioCadastroProcesso")
	public List<Processo> getProcessoListForIdUsuarioCadastroProcesso() {
		return this.processoListForIdUsuarioCadastroProcesso;
	}

	public void setProcessoListForIdUsuarioCadastroProcesso(
			List<Processo> processoListForIdUsuarioCadastroProcesso) {
		this.processoListForIdUsuarioCadastroProcesso = processoListForIdUsuarioCadastroProcesso;
	}
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "usuario")
	public List<BloqueioUsuario> getBloqueioUsuarioList() {
		return this.bloqueioUsuarioList;
	}

	public void setBloqueioUsuarioList(List<BloqueioUsuario> bloqueioUsuarioList) {
		this.bloqueioUsuarioList = bloqueioUsuarioList;
	}

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "usuario")
	public List<Endereco> getEnderecoList() {
		return this.enderecoList;
	}

	public void setEnderecoList(List<Endereco> enderecoList) {
		this.enderecoList = enderecoList;
	}

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "usuarioInclusao")
	public List<ProcessoDocumento> getProcessoDocumentoListForIdUsuarioInclusao() {
		return this.processoDocumentoListForIdUsuarioInclusao;
	}

	public void setProcessoDocumentoListForIdUsuarioInclusao(
			List<ProcessoDocumento> processoDocumentoListForIdUsuarioInclusao) {
		this.processoDocumentoListForIdUsuarioInclusao = processoDocumentoListForIdUsuarioInclusao;
	}
	
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "usuarioExclusao")
	public List<ProcessoDocumento> getProcessoDocumentoListForIdUsuarioExclusao() {
		return this.processoDocumentoListForIdUsuarioExclusao;
	}

	public void setProcessoDocumentoListForIdUsuarioExclusao(
			List<ProcessoDocumento> processoDocumentoListForIdUsuarioExclusao) {
		this.processoDocumentoListForIdUsuarioExclusao = processoDocumentoListForIdUsuarioExclusao;
	}

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "usuario")	
	public List<EntityLog> getEntityLogList() {
		return entityLogList;
	}
	
	public void setEntityLogList(List<EntityLog> entityLogList) {
		this.entityLogList = entityLogList;
	}
	
	@Transient
	public Localizacao[] getLocalizacoes() {
		Localizacao[] locs = new Localizacao[usuarioLocalizacaoList.size()];
		int i = 0;
		for (UsuarioLocalizacao uloc : usuarioLocalizacaoList) {
			locs[i] = uloc.getLocalizacao();
			i++;
		}
		return locs;
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
	
	public UsuarioLogin loadDataFromPessoaFisica (PessoaFisica pessoa) {
		setCpf(pessoa.getCpf());
		setDataNascimento(pessoa.getDataNascimento());
		setEmail(pessoa.getEmail());
		setIdPessoa(pessoa.getIdPessoa());
		setNome(pessoa.getNome());
		setTipoPessoa(pessoa.getTipoPessoa());
		setAtivo(pessoa.getAtivo());
		setTemContaTwitter(false);
		return this;
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
	
}