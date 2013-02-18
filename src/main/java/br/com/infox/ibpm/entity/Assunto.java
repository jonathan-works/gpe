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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import br.com.infox.annotations.ChildList;
import br.com.infox.annotations.HierarchicalPath;
import br.com.infox.annotations.Parent;
import br.com.infox.annotations.PathDescriptor;
import br.com.infox.annotations.Recursive;
/**
 * Assunto generated by hbm2java
 */

@Entity
@Table(name = "tb_assunto", schema="public")
@Recursive
public class Assunto implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int idAssunto;
	private Assunto assuntoPai;
	private String codAssunto;
	private String assunto;
	private Boolean ativo = Boolean.TRUE;
	private String caminhoCompleto;
	private List<Assunto> assuntoList = new ArrayList<Assunto>(0);

	public Assunto() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_assunto")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_assunto", unique = true, nullable = false)
	public int getIdAssunto() {
		return this.idAssunto;
	}

	public void setIdAssunto(int idAssunto) {
		this.idAssunto = idAssunto;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_assunto_pai")
	@Parent
	public Assunto getAssuntoPai() {
		return this.assuntoPai;
	}

	public void setAssuntoPai(Assunto assuntoPai) {
		this.assuntoPai = assuntoPai;
	}

	@Column(name = "cd_assunto", length = 30)
	@Size(max = 30)
	public String getCodAssunto() {
		return this.codAssunto;
	}

	public void setCodAssunto(String codAssunto) {
		this.codAssunto = codAssunto;
	}

	@Column(name = "ds_assunto", nullable = false, length = 100)
	@NotNull
	@Size(max = 100)
	@PathDescriptor
	public String getAssunto() {
		return this.assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	@Column(name = "in_ativo", nullable = false)
	@NotNull
	public Boolean getAtivo() {
		return this.ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "assuntoPai")
	@ChildList
	public List<Assunto> getAssuntoList() {
		return this.assuntoList;
	}

	public void setAssuntoList(List<Assunto> assuntoList) {
		this.assuntoList = assuntoList;
	}

	@Column(name="ds_caminho_completo")
	@HierarchicalPath
	public String getCaminhoCompleto() {
		return caminhoCompleto;
	}
	
	public void setCaminhoCompleto(String caminhoCompleto) {
		this.caminhoCompleto = caminhoCompleto;
	}
	
	@Override
	public String toString() {
		return caminhoCompleto.replace('|', '>').substring(0, caminhoCompleto.length()-1);
	}
	
	@Transient
	public List<Assunto> getListAssuntoAtePai() {
		List<Assunto> list = new ArrayList<Assunto>();
		Assunto assuntoPai = getAssuntoPai();
		while (assuntoPai != null) {
			list.add(assuntoPai);
			assuntoPai = assuntoPai.getAssuntoPai();
		}
		return list;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Assunto)) {
			return false;
		}
		Assunto other = (Assunto) obj;
		if (getIdAssunto() != other.getIdAssunto()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdAssunto();
		return result;
	}
}