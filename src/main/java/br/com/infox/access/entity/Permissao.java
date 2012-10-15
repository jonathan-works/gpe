package br.com.infox.access.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.jboss.seam.annotations.security.permission.PermissionAction;
import org.jboss.seam.annotations.security.permission.PermissionDiscriminator;
import org.jboss.seam.annotations.security.permission.PermissionRole;
import org.jboss.seam.annotations.security.permission.PermissionTarget;
import org.jboss.seam.annotations.security.permission.PermissionUser;

@Entity
@Table(name = "tb_permissao", schema="public")
public class Permissao implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long idPermissao;
	private String destinatario;
	private String alvo;
	private String acao;
	private String discriminador;

	@Id
	@GeneratedValue
	@Column(name = "id_permissao")
	public Long getIdPermissao() {
		return idPermissao;
	}

	public void setIdPermissao(Long id) {
		this.idPermissao = id;
	}

	@PermissionUser
	@PermissionRole
	@Column(name = "ds_destinatario")
	public String getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}

	@PermissionTarget
	@Column(name = "ds_alvo")
	public String getAlvo() {
		return alvo;
	}

	public void setAlvo(String target) {
		this.alvo = target;
	}

	@PermissionAction
	@Column(name = "ds_acao")
	public String getAcao() {
		return acao;
	}

	public void setAcao(String action) {
		this.acao = action;
	}

	@PermissionDiscriminator
	@Column(name = "ds_discriminador")
	public String getDiscriminador() {
		return discriminador;
	}

	public void setDiscriminador(String discriminator) {
		this.discriminador = discriminator;
	}

	@Override
	public String toString() {
		return alvo + ":" + acao;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getIdPermissao() == null) {
			return false;
		}
		if (!(obj instanceof Permissao)) {
			return false;
		}
		Permissao other = (Permissao) obj;
		return getIdPermissao().equals(other.getIdPermissao());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getIdPermissao() == null) ? 0 : getIdPermissao().hashCode());
		return result;
	}
}