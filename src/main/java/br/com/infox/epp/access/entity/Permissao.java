package br.com.infox.epp.access.entity;

import static br.com.infox.core.constants.LengthConstants.*;
import static br.com.infox.core.persistence.ORConstants.*;
import static br.com.infox.epp.access.query.PermissaoQuery.*;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.jboss.seam.annotations.security.permission.PermissionAction;
import org.jboss.seam.annotations.security.permission.PermissionDiscriminator;
import org.jboss.seam.annotations.security.permission.PermissionRole;
import org.jboss.seam.annotations.security.permission.PermissionTarget;
import org.jboss.seam.annotations.security.permission.PermissionUser;

@Entity
@Table(name = TABLE_PERMISSAO, schema=PUBLIC)
public class Permissao implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long idPermissao;
	private String destinatario;
	private String alvo;
	private String acao;
	private String discriminador;

	@Id
	@GeneratedValue
	@Column(name = ID_PERMISSAO)
	public Long getIdPermissao() {
		return idPermissao;
	}

	public void setIdPermissao(Long id) {
		this.idPermissao = id;
	}

	@PermissionUser
	@PermissionRole
	@Column(name = DESTINATARIO, length=DESCRICAO_GRANDE)
	@Size(max=DESCRICAO_GRANDE)
	public String getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}

	@PermissionTarget
	@Column(name = ALVO, length=DESCRICAO_GRANDE)
	@Size(max=DESCRICAO_GRANDE)
	public String getAlvo() {
		return alvo;
	}

	public void setAlvo(String target) {
		this.alvo = target;
	}

	@PermissionAction
	@Column(name = ACAO, length=DESCRICAO_GRANDE)
	@Size(max=DESCRICAO_GRANDE)
	public String getAcao() {
		return acao;
	}

	public void setAcao(String action) {
		this.acao = action;
	}

	@PermissionDiscriminator
	@Column(name = DISCRIMINADOR, length=DESCRICAO_GRANDE)
	@Size(max=DESCRICAO_GRANDE)
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