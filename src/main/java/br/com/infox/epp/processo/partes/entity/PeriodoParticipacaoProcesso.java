package br.com.infox.epp.processo.partes.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = PeriodoParticipacaoProcesso.TABLE_NAME)
public class PeriodoParticipacaoProcesso implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_periodo_participacao_proc";
	
	@Id
	@GeneratedValue(generator = "ParticipacaoGenerator")
	@SequenceGenerator(initialValue=1, allocationSize=1, sequenceName="id_periodo_participacao_proc", name="ParticipacaoGenerator")
	@Column(name = "id_periodo_participacao_proc", nullable = false, unique = true)
	private Long id;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_participante_processo", nullable = false)
	private ParticipanteProcesso participanteProcesso;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_inicio", nullable = false)
	private Date dataInicio;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_fim")
	private Date dataFim;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ParticipanteProcesso getParticipanteProcesso() {
		return participanteProcesso;
	}

	public void setParticipanteProcesso(ParticipanteProcesso participanteProcesso) {
		this.participanteProcesso = participanteProcesso;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PeriodoParticipacaoProcesso))
			return false;
		PeriodoParticipacaoProcesso other = (PeriodoParticipacaoProcesso) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
	
}
