package br.com.infox.ibpm.jbpm.task.entity;

import java.io.Serializable;

import javax.persistence.*;

import br.com.infox.epp.access.entity.UsuarioLogin;

@Entity
@Table(name = UsuarioTaskInstance.TABLE_NAME, schema="public")
public class UsuarioTaskInstance implements Serializable {
	
	public static final String TABLE_NAME = "tb_usuario_taskinstance";
	private static final long serialVersionUID = 1L;

	private Long idTaskInstance;
	private UsuarioLogin usuario;

	public UsuarioTaskInstance() {
	}
	
	public UsuarioTaskInstance(final Long idTaskinstance, final UsuarioLogin usuario) {
		this.idTaskInstance = idTaskinstance;
		this.usuario = usuario;
	}
	
	@Id
	@Column(name = "id_taskinstance", unique = true, nullable = false)
	public Long getIdTaskInstance() {
		return idTaskInstance;
	}

	public void setIdTaskInstance(Long idTaskInstance) {
		this.idTaskInstance = idTaskInstance;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_login")
	public UsuarioLogin getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioLogin usuario) {
		this.usuario = usuario;
	}
	
	
}
