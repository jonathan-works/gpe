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
package br.com.infox.epp.fluxo.entity;

import static br.com.infox.core.persistence.ORConstants.*;

import java.io.Serializable;
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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.access.entity.UsuarioLogin;
import static br.com.infox.epp.fluxo.query.FluxoQuery.*;
import br.com.itx.util.HibernateUtil;

@Entity
@Table(name = TABLE_FLUXO, schema=PUBLIC, uniqueConstraints={
    @UniqueConstraint(columnNames={DESCRICAO_FLUXO})
})
@NamedQueries(value={
    @NamedQuery(name=LIST_ATIVOS, query=LIST_ATIVOS_QUERY),
    @NamedQuery(name=COUNT_PROCESSOS_ATRASADOS, query=COUNT_PROCESSOS_ATRASADOS_QUERY),
    @NamedQuery(name=FLUXO_BY_NAME, query=FLUXO_BY_NAME_QUERY)
})
public class Fluxo implements Serializable {

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

	@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_FLUXO)
	@Id
	@GeneratedValue(generator = GENERATOR)
	@Column(name = ID_FLUXO, unique = true, nullable = false)
	public Integer getIdFluxo() {
		return this.idFluxo;
	}

	public void setIdFluxo(Integer idFluxo) {
		this.idFluxo = idFluxo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = ID_USUARIO_PUBLICACAO)
	public UsuarioLogin getUsuarioPublicacao() {
		return this.usuarioPublicacao;
	}

	public void setUsuarioPublicacao(UsuarioLogin usuarioPublicacao) {
		this.usuarioPublicacao = usuarioPublicacao;
	}

	@Column(name = CODIGO_FLUXO, length=LengthConstants.DESCRICAO_PEQUENA)
	@Size(max=LengthConstants.DESCRICAO_PEQUENA)
	public String getCodFluxo() {
		return this.codFluxo;
	}

	public void setCodFluxo(String codFluxo) {
	    this.codFluxo = codFluxo;
	    if (codFluxo != null){
	        this.codFluxo = codFluxo.trim();
	    }
	}

	@Column(name = DESCRICAO_FLUXO, nullable = false, length=LengthConstants.DESCRICAO_PADRAO, unique = true)
	@Size(max=LengthConstants.DESCRICAO_PADRAO)
	@NotNull
	public String getFluxo() {
		return this.fluxo;
	}

	public void setFluxo(String fluxo) {
	    this.fluxo = fluxo;
	    if (fluxo != null) {
			this.fluxo = fluxo.trim();
		}
	}

	@Column(name = XML_FLUXO)
	public String getXml() {
		return this.xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	@Column(name = ATIVO, nullable = false)
	@NotNull
	
	public Boolean getAtivo() {
		return this.ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	@Column(name = PRAZO, nullable=true)
	public Integer getQtPrazo() {
		return this.qtPrazo;
	}

	public void setQtPrazo(Integer qtPrazo) {
		this.qtPrazo = qtPrazo;
	}

	@Column(name = PUBLICADO, nullable = false)
	@NotNull
	public Boolean getPublicado() {
		return this.publicado;
	}

	public void setPublicado(Boolean publicado) {
		this.publicado = publicado;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = DATA_INICIO_PUBLICACAO)
	public Date getDataInicioPublicacao() {
		return this.dataInicioPublicacao;
	}

	public void setDataInicioPublicacao(Date dataInicioPublicacao) {
		this.dataInicioPublicacao = dataInicioPublicacao;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = DATA_FIM_PUBLICACAO)
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
		if (!getIdFluxo().equals(other.getIdFluxo())) {
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = FLUXO_ATTRIBUTE)
	public List<FluxoPapel> getFluxoPapelList() {
		return fluxoPapelList;
	}
}