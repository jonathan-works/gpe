package br.com.infox.access.entity;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.security.management.UserEnabled;
import org.jboss.seam.annotations.security.management.UserFirstName;
import org.jboss.seam.annotations.security.management.UserPassword;
import org.jboss.seam.annotations.security.management.UserPrincipal;
import org.jboss.seam.annotations.security.management.UserRoles;

import br.com.infox.access.query.UsuarioLoginQuery;
import br.com.itx.util.StringUtil;

@Entity
@Table(name = "tb_usuario_login", schema="public" , uniqueConstraints = @UniqueConstraint(columnNames = "ds_login"))
@NamedQuery(name=UsuarioLoginQuery.USUARIO_LOGIN_NAME, query=UsuarioLoginQuery.USUARIO_LOGIN_QUERY)
@Inheritance(strategy=InheritanceType.JOINED)
@BypassInterceptors
public class UsuarioLogin implements UsuarioLoginQuery, Serializable {

	private static final long serialVersionUID = 1L;

	private Integer idUsuario;
	private String senha;
	private String email;
	private String login;
	private String nome;
	private String assinatura;
	private String certChain;
	private Boolean ldap;
	private Boolean ativo;

	private Set<Papel> papelSet = new TreeSet<Papel>();


	public UsuarioLogin() {
	}

	@Id
	@GeneratedValue
	@Column(name = "id_usuario", unique = true, nullable = false)
	public Integer getIdUsuario() {
		return this.idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	@Column(name = "ds_senha", length = 100)
	@Length(max = 100)
	@UserPassword(hash = "SHA")
	public String getSenha() {
		return this.senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Column(name = "ds_email", length = 100)
	@Length(max = 100)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "ds_login", unique = true, nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	@UserPrincipal
	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Column(name = "ds_nome", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	@UserFirstName
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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
	

	@Column(name = "in_ativo", nullable = false)
	@NotNull
	@UserEnabled
	public Boolean getAtivo() {
		return this.ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
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
	
	@Override
	public String toString() {
		return nome;
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
		if (getIdUsuario() == null) {
			return false;
		}
		if (!(obj instanceof UsuarioLogin)) {
			return false;
		}
		UsuarioLogin other = (UsuarioLogin) obj;
		return getIdUsuario().equals(other.getIdUsuario());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getIdUsuario() == null) ? 0 : getIdUsuario().hashCode());
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
	
}