package br.com.infox.ibpm.entity;
// Generated 30/10/2008 07:40:27 by Hibernate Tools 3.2.0.CR1

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import br.com.infox.ibpm.type.TarefaEventoEnum;

/**
 * Infox
 */
@Entity
@Table(name = TarefaEvento.TABLE_NAME, schema="public")
public class TarefaEvento implements java.io.Serializable {

	public static final String TABLE_NAME = "tb_tarefa_evento";
	private static final long serialVersionUID = 1L;

	private int idTarefaEvento;
	private Tarefa tarefa;
	private Tarefa tarefaOrigem;
	private TarefaEventoEnum evento;
	private List<TarefaEventoAgrupamento> tarefaEventoAgrupamentoList = 
			new ArrayList<TarefaEventoAgrupamento>(0);
	private List<ProcessoTarefaEvento> processoTarefaEventoList = 
			new ArrayList<ProcessoTarefaEvento>(0);
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_tarefa_evento")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_tarefa_evento", unique = true, nullable = false)
	public int getIdTarefaEvento() {
		return idTarefaEvento;
	}
	
	public void setIdTarefaEvento(int idTarefaEvento) {
		this.idTarefaEvento = idTarefaEvento;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_tarefa", nullable=false)
	public Tarefa getTarefa() {
		return tarefa;
	}

	public void setTarefa(Tarefa tarefa) {
		this.tarefa = tarefa;
	}	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_tarefa_origem")
	public Tarefa getTarefaOrigem() {
		return tarefaOrigem;
	}

	public void setTarefaOrigem(Tarefa tarefaOrigem) {
		this.tarefaOrigem = tarefaOrigem;
	}
	
	@Column(name = "in_evento", length = 2)
	@Enumerated(EnumType.STRING)
	public TarefaEventoEnum getEvento() {
		return evento;
	}

	public void setEvento(TarefaEventoEnum evento) {
		this.evento = evento;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tarefaEvento", 
			   cascade={CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE})
	public List<TarefaEventoAgrupamento> getTarefaEventoAgrupamentoList() {
		return tarefaEventoAgrupamentoList;
	}

	public void setTarefaEventoAgrupamentoList(
			List<TarefaEventoAgrupamento> tarefaEventoAgrupamentoList) {
		this.tarefaEventoAgrupamentoList = tarefaEventoAgrupamentoList;
	}

	@Override
	public String toString() {
		return evento.getLabel();
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tarefaEvento", 
			   cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	public List<ProcessoTarefaEvento> getProcessoTarefaEventoList() {
		return processoTarefaEventoList;
	}

	public void setProcessoTarefaEventoList(
			List<ProcessoTarefaEvento> processoTarefaEventoList) {
		this.processoTarefaEventoList = processoTarefaEventoList;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TarefaEvento)) {
			return false;
		}
		TarefaEvento other = (TarefaEvento) obj;
		if (getIdTarefaEvento() != other.getIdTarefaEvento()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdTarefaEvento();
		return result;
	}
}