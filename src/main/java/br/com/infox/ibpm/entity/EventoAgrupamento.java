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
 * Grupo Evento Agrupamento generated by Infox
 */
@Entity
@Table(name = EventoAgrupamento.TABLE_NAME, schema="public",
	   uniqueConstraints={@UniqueConstraint(columnNames={"id_evento","id_agrupamento"})})
public class EventoAgrupamento implements java.io.Serializable {

	public static final String TABLE_NAME = "tb_evento_agrupamento";
	private static final long serialVersionUID = 1L;

	private int idEventoAgrupamento;
	private Evento evento;
	private Agrupamento agrupamento;
	private Boolean multiplo; 
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_evento_agrupamento")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_evento_agrupamento", unique = true, nullable = false)
	public int getIdEventoAgrupamento() {
		return idEventoAgrupamento;
	}
	
	public void setIdEventoAgrupamento(int idEventoAgrupamento) {
		this.idEventoAgrupamento = idEventoAgrupamento;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evento", nullable = false)
	@NotNull
	public Evento getEvento() {
		return evento;
	}
	
	public void setEvento(Evento evento) {
		this.evento = evento;
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
	
	@Override
	public String toString() {
		return evento.toString();
	}

	@Column(name="in_multiplo", nullable=false)
	@NotNull
	public Boolean getMultiplo() {
		return multiplo;
	}

	public void setMultiplo(Boolean multiplo) {
		this.multiplo = multiplo;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof EventoAgrupamento)) {
			return false;
		}
		EventoAgrupamento other = (EventoAgrupamento) obj;
		if (getIdEventoAgrupamento() != other.getIdEventoAgrupamento()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdEventoAgrupamento();
		return result;
	}
}