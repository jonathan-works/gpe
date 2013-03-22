package br.com.infox.ibpm.jbpm;

import java.io.Serializable;

import javax.persistence.*;

import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.access.entity.UsuarioLogin;

@Entity
@Table(name = UsuarioTaskInstance.TABLE_NAME, schema="public")
@BypassInterceptors
public class UsuarioTaskInstance implements Serializable {
	
	public static final String TABLE_NAME = "tb_usuario_taskinstance";
	private static final long serialVersionUID = 1L;

	private Long idTaskInstance;
	private UsuarioLogin usuario;

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
