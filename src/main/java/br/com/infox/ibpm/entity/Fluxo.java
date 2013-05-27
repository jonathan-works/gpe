/*
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.epp.query.FluxoQuery;
import br.com.itx.util.HibernateUtil;

@Entity
@Table(name = "tb_fluxo", schema="public")
@NamedQueries(value={
	@NamedQuery(name=FluxoQuery.LIST_ATIVOS,
			    query=FluxoQuery.LIST_ATIVOS_QUERY),
	@NamedQuery(name=FluxoQuery.COUNT_PROCESSOS_ATRASADOS,
			    query=FluxoQuery.COUNT_PROCESSOS_ATRASADOS_QUERY)
			    
  })
public class Fluxo implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Integer idFluxo;
	private UsuarioLogin usuarioPublicacao;
	private String codFluxo;
	private String fluxo;
	private Boolean ativo;
	private Integer qtPrazo;
	private Boolean publicado;
	private Date dataInicioPublicacao;
	private Date dataFimPublicacao;
	
	private String xml;
	
	private List<FluxoPapel> fluxoPapelList = new ArrayList<FluxoPapel>(0); 

	public Fluxo() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_fluxo")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_fluxo", unique = true, nullable = false)
	public Integer getIdFluxo() {
		return this.idFluxo;
	}

	public void setIdFluxo(Integer idFluxo) {
		this.idFluxo = idFluxo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_publicacao")
	public UsuarioLogin getUsuarioPublicacao() {
		return this.usuarioPublicacao;
	}

	public void setUsuarioPublicacao(UsuarioLogin usuarioPublicacao) {
		this.usuarioPublicacao = usuarioPublicacao;
	}

	@Column(name = "cd_fluxo", length = 30)
	@Size(max = 30)
	public String getCodFluxo() {
		return this.codFluxo;
	}

	public void setCodFluxo(String codFluxo) {
		if (codFluxo != null) {
			codFluxo = codFluxo.trim();
		}		
		this.codFluxo = codFluxo;
	}

	@Column(name = "ds_fluxo", nullable = false, length = 100, unique = true)
	@NotNull
	@Size(max = 100)
	public String getFluxo() {
		return this.fluxo;
	}

	public void setFluxo(String fluxo) {
		if (fluxo != null) {
			fluxo = fluxo.trim();
		}
		this.fluxo = fluxo;
	}

	@Column(name = "ds_xml")
	public String getXml() {
		return this.xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	@Column(name = "in_ativo", nullable = false)
	@NotNull
	
	public Boolean getAtivo() {
		return this.ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	@Column(name = "qt_prazo", nullable=true)
	public Integer getQtPrazo() {
		return this.qtPrazo;
	}

	public void setQtPrazo(Integer qtPrazo) {
		this.qtPrazo = qtPrazo;
	}

	@Column(name = "in_publicado", nullable = false)
	@NotNull
	public Boolean getPublicado() {
		return this.publicado;
	}

	public void setPublicado(Boolean publicado) {
		this.publicado = publicado;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_inicio_publicacao")
	public Date getDataInicioPublicacao() {
		return this.dataInicioPublicacao;
	}

	public void setDataInicioPublicacao(Date dataInicioPublicacao) {
		this.dataInicioPublicacao = dataInicioPublicacao;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_fim_publicacao")
	public Date getDataFimPublicacao() {
		return this.dataFimPublicacao;
	}

	public void setDataFimPublicacao(Date dataFimPublicacao) {
		this.dataFimPublicacao = dataFimPublicacao;
	}
	
	@Override
	public String toString() {
		return fluxo;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Fluxo)) {
			return false;
		}
		Fluxo other = (Fluxo) HibernateUtil.removeProxy(obj);
		if (getIdFluxo() != other.getIdFluxo()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdFluxo();
		return result;
	}

	public void setFluxoPapelList(List<FluxoPapel> fluxoPapelList) {
		this.fluxoPapelList = fluxoPapelList;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fluxo")
	public List<FluxoPapel> getFluxoPapelList() {
		return fluxoPapelList;
	}
}