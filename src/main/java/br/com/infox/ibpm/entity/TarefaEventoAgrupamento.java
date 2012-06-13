package br.com.infox.ibpm.entity;
// Generated 30/10/2008 07:40:27 by Hibernate Tools 3.2.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.NotNull;

/**
 * Infox
 */
@Entity
@Table(name = TarefaEventoAgrupamento.TABLE_NAME, schema="public",
		   uniqueConstraints={@UniqueConstraint(columnNames={"id_agrupamento", "id_tarefa_evento"})})
public class TarefaEventoAgrupamento implements java.io.Serializable {

	public static final String TABLE_NAME = "tb_tarefa_evento_agrupamento";
	private static final long serialVersionUID = 1L;

	private int idTarefaEventoAgrupamento;
	private Agrupamento agrupamento;
	private TarefaEvento tarefaEvento;
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_tarefa_evento_agrupamento")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_tarefa_evento_agrupamento", unique = true, nullable = false)
	public int getIdTarefaEventoAgrupamento() {
		return idTarefaEventoAgrupamento;
	}
	
	public void setIdTarefaEventoAgrupamento(int idTarefaEventoAgrupamento) {
		this.idTarefaEventoAgrupamento = idTarefaEventoAgrupamento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_agrupamento", nullable = false)
	@NotNull
	public Agrupamento getAgrupamento() {
		return agrupamento;
	}

	public void setAgrupamento(Agrupamento agrupamento) {
		this.agrupamento = agrupamento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tarefa_evento", nullable = false)
	@NotNull
	public TarefaEvento getTarefaEvento() {
		return tarefaEvento;
	}

	public void setTarefaEvento(TarefaEvento tarefaEvento) {
		this.tarefaEvento = tarefaEvento;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TarefaEventoAgrupamento)) {
			return false;
		}
		TarefaEventoAgrupamento other = (TarefaEventoAgrupamento) obj;
		if (getIdTarefaEventoAgrupamento() != other.getIdTarefaEventoAgrupamento()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdTarefaEventoAgrupamento();
		return result;
	}
}