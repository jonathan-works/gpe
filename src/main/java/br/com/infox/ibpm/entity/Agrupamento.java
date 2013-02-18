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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

/**
 * Agrupamento Evento generated by Infox
 */
@Entity
@Table(name = Agrupamento.TABLE_NAME, schema="public",
	   uniqueConstraints={@UniqueConstraint(columnNames={"ds_agrupamento"})})
public class Agrupamento implements java.io.Serializable {

	public static final String TABLE_NAME = "tb_agrupamento";
	private static final long serialVersionUID = 1L;

	private int idAgrupamento;
	private String agrupamento;
	private Boolean ativo = Boolean.TRUE;
	private List<EventoAgrupamento> eventoAgrupamentoList = new ArrayList<EventoAgrupamento>(0);
	private List<TarefaEventoAgrupamento> agrupamentoTarefaList = 
				new ArrayList<TarefaEventoAgrupamento>(0);
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_agrupamento")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_agrupamento", unique = true, nullable = false)
	public int getIdAgrupamento() {
		return idAgrupamento;
	}
	
	public void setIdAgrupamento(int idAgrupamento) {
		this.idAgrupamento = idAgrupamento;
	}
	
	@Column(name = "ds_agrupamento", nullable = false, length = 200)
	@NotNull
	@Size(max = 200)
	public String getAgrupamento() {
		return agrupamento;
	}
	
	public void setAgrupamento(String agrupamento) {
		this.agrupamento = agrupamento;
	}
	
	@Column(name = "in_ativo", nullable = false)
	@NotNull
	public Boolean getAtivo() {
		return ativo;
	}
	
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, 
			   mappedBy = "agrupamento")
	public List<EventoAgrupamento> getEventoAgrupamentoList() {
		return eventoAgrupamentoList;
	}
	
	public void setEventoAgrupamentoList(List<EventoAgrupamento> eventoAgrupamentoList) {
		this.eventoAgrupamentoList = eventoAgrupamentoList;
	}
	
	@Override
	public String toString() {
		return agrupamento;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, 
			   mappedBy = "agrupamento")
	public List<TarefaEventoAgrupamento> getAgrupamentoTarefaList() {
		return agrupamentoTarefaList;
	}

	public void setAgrupamentoTarefaList(
			List<TarefaEventoAgrupamento> agrupamentoTarefaList) {
		this.agrupamentoTarefaList = agrupamentoTarefaList;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Agrupamento)) {
			return false;
		}
		Agrupamento other = (Agrupamento) obj;
		if (getIdAgrupamento() != other.getIdAgrupamento()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdAgrupamento();
		return result;
	}
}