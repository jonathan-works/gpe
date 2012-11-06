package br.com.infox.ibpm.jbpm;

import java.io.Serializable;

import javax.persistence.*;

import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Entity
@Table(name = UsuarioTaskInstance.TABLE_NAME, schema="public")
@BypassInterceptors
public class UsuarioTaskInstance implements Serializable {
	
	public static final String TABLE_NAME = "tb_usuario_taskinstance";
	private static final long serialVersionUID = 1L;

	private Long idTaskInstance;
	
	private Integer idUsuario;

	@Id
	@Column(name = "id_taskinstance", unique = true, nullable = false)
	public Long getIdTaskInstance() {
		return idTaskInstance;
	}

	public void setIdTaskInstance(Long idTaskInstance) {
		this.idTaskInstance = idTaskInstance;
	}

	@Column(name = "id_usuario_login")
	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
	
	
}
