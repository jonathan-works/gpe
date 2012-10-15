/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.entity;
// Generated 30/10/2008 07:40:27 by Hibernate Tools 3.2.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.NotNull;
/**
 * BloqueioUsuario generated by hbm2java
 */
@Entity
@Table(name = "tb_bloqueio_usuario", schema="public")
public class BloqueioUsuario implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Integer idBloqueioUsuario;
	private Usuario usuario;
	private Date dataBloqueio;
	private Date dataPrevisaoDesbloqueio;
	private String motivoBloqueio;
	private Date dataDesbloqueio;

	public BloqueioUsuario() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_bloqueio_usuario")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_bloqueio_usuario", unique = true, nullable = false)
	public Integer getIdBloqueioUsuario() {
		return this.idBloqueioUsuario;
	}

	public void setIdBloqueioUsuario(Integer idBloqueioUsuario) {
		this.idBloqueioUsuario = idBloqueioUsuario;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario", nullable = false)
	@NotNull
	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_bloqueio", nullable = false)
	@NotNull
	public Date getDataBloqueio() {
		return this.dataBloqueio;
	}

	public void setDataBloqueio(Date dataBloqueio) {
		this.dataBloqueio = dataBloqueio;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_previsao_desbloqueio", nullable = false)
	@NotNull
	public Date getDataPrevisaoDesbloqueio() {
		return this.dataPrevisaoDesbloqueio;
	}

	public void setDataPrevisaoDesbloqueio(Date dataPrevisaoDesbloqueio) {
		this.dataPrevisaoDesbloqueio = dataPrevisaoDesbloqueio;
	}

	@Column(name = "ds_motivo_bloqueio", nullable = false)
	@NotNull
	public String getMotivoBloqueio() {
		return this.motivoBloqueio;
	}

	public void setMotivoBloqueio(String motivoBloqueio) {
		this.motivoBloqueio = motivoBloqueio;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_desbloqueio")
	public Date getDataDesbloqueio() {
		return this.dataDesbloqueio;
	}

	public void setDataDesbloqueio(Date dataDesbloqueio) {
		this.dataDesbloqueio = dataDesbloqueio;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof BloqueioUsuario)) {
			return false;
		}
		BloqueioUsuario other = (BloqueioUsuario) obj;
		return getIdBloqueioUsuario() == other.getIdBloqueioUsuario();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdBloqueioUsuario();
		return result;
	}
}