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


/**
 * Infox
 */
@Entity
@Table(name = ProcessoTarefaEvento.TABLE_NAME, schema="public")
public class ProcessoTarefaEvento implements java.io.Serializable {

	public static final String TABLE_NAME = "tb_processo_tarefa_evento";
	private static final long serialVersionUID = 1L;

	private int idProcessoTarefaEvento;
	private TarefaEvento tarefaEvento;
	private Processo processo;
	private Boolean registrado;
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_processo_tarefa_evento")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_processo_tarefa_evento", unique = true, nullable = false)
	public int getIdProcessoTarefaEvento() {
		return idProcessoTarefaEvento;
	}
	
	public void setIdProcessoTarefaEvento(int idProcessoTarefaEvento) {
		this.idProcessoTarefaEvento = idProcessoTarefaEvento;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_tarefa_evento", nullable=false)
	public TarefaEvento getTarefaEvento() {
		return tarefaEvento;
	}

	public void setTarefaEvento(TarefaEvento tarefaEvento) {
		this.tarefaEvento = tarefaEvento;
	}	

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_processo", nullable=false)
	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	@Column(name = "in_registrado")
	public Boolean getRegistrado() {
		return registrado;
	}

	public void setRegistrado(Boolean registrado) {
		this.registrado = registrado;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ProcessoTarefaEvento)) {
			return false;
		}
		ProcessoTarefaEvento other = (ProcessoTarefaEvento) obj;
		if (getIdProcessoTarefaEvento() != other.getIdProcessoTarefaEvento()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdProcessoTarefaEvento();
		return result;
	}
}