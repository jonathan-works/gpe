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
import javax.persistence.UniqueConstraint;

// Generated 30/10/2008 07:40:27 by Hibernate Tools 3.2.0.CR1


/**
 * Infox
 */
@Entity
@Table(name = TarefaTransicaoEvento.TABLE_NAME, schema="public",
	   uniqueConstraints={@UniqueConstraint(columnNames={"id_tarefa_origem", "id_tarefa_destino"})})
public class TarefaTransicaoEvento implements java.io.Serializable {

	public static final String TABLE_NAME = "tb_tarefa_transicao_evento";
	private static final long serialVersionUID = 1L;

	private int idVerificaEvento;
	private Tarefa tarefaOrigem;
	private Tarefa tarefaDestino;
	private List<TarefaTransicaoEventoAgrupamento> tarefaTransicaoEventoAgrupamentoList = new ArrayList<TarefaTransicaoEventoAgrupamento>(0);

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_tarefa_transicao_evento")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_tarefa_transicao_evento", unique = true, nullable = false)
	public int getIdVerificaEvento() {
		return idVerificaEvento;
	}
	
	public void setIdVerificaEvento(int idVerificaEvento) {
		this.idVerificaEvento = idVerificaEvento;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_tarefa_origem", nullable=false)
	public Tarefa getTarefaOrigem() {
		return tarefaOrigem;
	}

	public void setTarefaOrigem(Tarefa tarefaOrigem) {
		this.tarefaOrigem = tarefaOrigem;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_tarefa_destino", nullable=false)
	public Tarefa getTarefaDestino() {
		return tarefaDestino;
	}

	public void setTarefaDestino(Tarefa tarefaDestino) {
		this.tarefaDestino = tarefaDestino;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tarefaTransicaoEvento", 
			   cascade={CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE})
	public List<TarefaTransicaoEventoAgrupamento> getTarefaTransicaoEventoAgrupamentoList() {
		return tarefaTransicaoEventoAgrupamentoList;
	}

	public void setTarefaTransicaoEventoAgrupamentoList(
			List<TarefaTransicaoEventoAgrupamento> tarefaTransicaoEventoAgrupamentoList) {
		this.tarefaTransicaoEventoAgrupamentoList = tarefaTransicaoEventoAgrupamentoList;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TarefaTransicaoEvento)) {
			return false;
		}
		TarefaTransicaoEvento other = (TarefaTransicaoEvento) obj;
		if (getIdVerificaEvento() != other.getIdVerificaEvento()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdVerificaEvento();
		return result;
	}
}