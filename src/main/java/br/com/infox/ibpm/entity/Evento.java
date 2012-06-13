package br.com.infox.ibpm.entity;
// Generated 30/10/2008 07:40:27 by Hibernate Tools 3.2.0.CR1

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import br.com.infox.annotations.ChildList;
import br.com.infox.annotations.HierarchicalPath;
import br.com.infox.annotations.Parent;
import br.com.infox.annotations.PathDescriptor;
import br.com.infox.annotations.Recursive;

/**
 * TipoEntidade generated by hiran
 */
@Entity
@Table(name = Evento.TABLE_NAME, schema="public")
@Inheritance(strategy=InheritanceType.JOINED)
@Recursive
public class Evento implements java.io.Serializable {

	public static final String TABLE_NAME = "tb_evento";
	private static final long serialVersionUID = 1L;

	private int idEvento;
	private String evento;
	private String observacao;
	private Boolean ativo = Boolean.TRUE;
	private Evento eventoSuperior;
	private Status status;
	private String caminhoCompleto;
	
	private List<Evento> eventoList = new ArrayList<Evento>(0);
	
	private List<TipoProcessoDocumento> tipoProcessoDocumentoList = new ArrayList<TipoProcessoDocumento>(0);	
	
	private List<EventoAgrupamento> eventoAgrupamentoList = new ArrayList<EventoAgrupamento>(0);
	
	public Evento() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_evento")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_evento", unique = true, nullable = false)
	public int getIdEvento() {
		return this.idEvento;
	}

	public void setIdEvento(int idEvento) {
		this.idEvento = idEvento;
	}
	
	@Column(name = "ds_evento", nullable = false, length = 100, unique=true)
	@NotNull
	@Length(max = 100)
	@PathDescriptor
	public String getEvento() {
		return this.evento;
	}
	
	public void setEvento(String evento) {
		this.evento = evento;
	}

	@Column(name = "ds_observacao")
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	
	@Column(name = "in_ativo", nullable = false)
	@NotNull
	public Boolean getAtivo() {
		return this.ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evento_superior")
	@Parent
	public Evento getEventoSuperior() {
		return this.eventoSuperior;
	}

	public void setEventoSuperior(Evento eventoSuperior) {
		this.eventoSuperior = eventoSuperior;
	}

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "eventoSuperior")
	@ChildList
	public List<Evento> getEventoList() {
		return this.eventoList;
	}

	public void setEventoList(List<Evento> eventoList) {
		this.eventoList = eventoList;
	}
	
	@Transient
	public List<Evento> getEventoListCompleto() {
		return getEventoListCompleto(this, new ArrayList<Evento>());
	}
	
	private List<Evento> getEventoListCompleto(Evento filho, List<Evento> eventos) {
		List<Evento> filhos = filho.getEventoList();
		eventos.add(filho);
		for (Evento evento : filhos) {
			getEventoListCompleto(evento, eventos);
		}
		return eventos;
	}		
	
	@Override
	public String toString() {
		return evento;
	}
	
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH}, fetch = FetchType.LAZY)
	@JoinTable(name = "tb_tipo_evento", joinColumns = {@JoinColumn(name = "id_evento", nullable = false, updatable = false)}, inverseJoinColumns = {@JoinColumn(name = "id_tipo_processo_documento", nullable = false, updatable = false)})
	public List<TipoProcessoDocumento> getTipoProcessoDocumentoList() {
		return tipoProcessoDocumentoList;
	}

	public void setTipoProcessoDocumentoList(List<TipoProcessoDocumento> tipoProcessoDocumentoList) {
		this.tipoProcessoDocumentoList = tipoProcessoDocumentoList;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, 
			   mappedBy = "evento")
	public List<EventoAgrupamento> getEventoAgrupamentoList() {
		return eventoAgrupamentoList;
	}

	public void setEventoAgrupamentoList(
			List<EventoAgrupamento> eventoAgrupamentoList) {
		this.eventoAgrupamentoList = eventoAgrupamentoList;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_status")
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
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
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Evento)) {
			return false;
		}
		Evento other = (Evento) obj;
		if (getIdEvento() != other.getIdEvento()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdEvento();
		return result;
	}	
}