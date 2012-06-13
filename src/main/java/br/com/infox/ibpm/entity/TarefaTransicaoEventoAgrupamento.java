package br.com.infox.ibpm.entity;

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
@Table(name = TarefaTransicaoEventoAgrupamento.TABLE_NAME, schema="public",
	   uniqueConstraints={@UniqueConstraint(columnNames={"id_agrupamento", "id_tarefa_transicao_evento"})})
public class TarefaTransicaoEventoAgrupamento implements java.io.Serializable {

	public static final String TABLE_NAME = "tb_tarefa_transicao_evento_agrupamento";
	private static final long serialVersionUID = 1L;

	private int idTarefaTransicaoEventoAgrupamento;
	private Agrupamento agrupamento;
	private TarefaTransicaoEvento tarefaTransicaoEvento;
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_tarefa_transicao_evento_agrupamento")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_tarefa_transicao_evento_agrupamento", unique = true, nullable = false)
	public int getIdTarefaTransicaoEventoAgrupamento() {
		return idTarefaTransicaoEventoAgrupamento;
	}
	
	public void setIdTarefaTransicaoEventoAgrupamento(
			int idTarefaTransicaoEventoAgrupamento) {
		this.idTarefaTransicaoEventoAgrupamento = idTarefaTransicaoEventoAgrupamento;
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
	@JoinColumn(name = "id_tarefa_transicao_evento", nullable = false)
	@NotNull
	public TarefaTransicaoEvento getTarefaTransicaoEvento() {
		return tarefaTransicaoEvento;
	}

	public void setTarefaTransicaoEvento(TarefaTransicaoEvento tarefaTransicaoEvento) {
		this.tarefaTransicaoEvento = tarefaTransicaoEvento;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TarefaTransicaoEventoAgrupamento)) {
			return false;
		}
		TarefaTransicaoEventoAgrupamento other = (TarefaTransicaoEventoAgrupamento) obj;
		if (getIdTarefaTransicaoEventoAgrupamento() != other.getIdTarefaTransicaoEventoAgrupamento()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdTarefaTransicaoEventoAgrupamento();
		return result;
	}
}