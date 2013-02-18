package br.com.infox.ibpm.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.validation.constraints.NotNull;

import br.com.infox.ibpm.entity.Evento;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.infox.access.entity.UsuarioLogin;

@Entity
@Table(name = "tb_processo_evento_temp", schema = "public")
public class ProcessoEventoTemp implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int idProcessoEventoTemp;
	private Evento evento;
	private ProcessoDocumento processoDocumento;
	private Processo processo;
	private UsuarioLogin usuario;
	private Date dataInsercao;
	private Long idJbpmTask;
	private TipoProcessoDocumento tipoProcessoDocumento;
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_processo_evento_temp")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_processo_evento_temp", unique = true, nullable = false)
	public int getIdProcessoEventoTemp() {
		return idProcessoEventoTemp;
	}

	public void setIdProcessoEventoTemp(int idProcessoEventoTemp) {
		this.idProcessoEventoTemp = idProcessoEventoTemp;
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
	@JoinColumn(name = "id_processo", nullable = false)
	@NotNull
	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	public UsuarioLogin getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioLogin usuario) {
		this.usuario = usuario;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dt_insercao")
	public Date getDataInsercao() {
		return dataInsercao;
	}

	public void setDataInsercao(Date dataInsercao) {
		this.dataInsercao = dataInsercao;
	}

	@Column(name = "id_jbpm_task")
	public Long getIdJbpmTask() {
		return idJbpmTask;
	}

	public void setIdJbpmTask(Long idJbpmTask) {
		this.idJbpmTask = idJbpmTask;
	}

	public void setProcessoDocumento(ProcessoDocumento processoDocumento) {
		this.processoDocumento = processoDocumento;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name="id_processo_documento")	
	public ProcessoDocumento getProcessoDocumento() {
		return processoDocumento;
	}

	public void setTipoProcessoDocumento(TipoProcessoDocumento tipoProcessoDocumento) {
		this.tipoProcessoDocumento = tipoProcessoDocumento;
	}
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name="id_tipo_processo_documento")
	public TipoProcessoDocumento getTipoProcessoDocumento() {
		return tipoProcessoDocumento;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ProcessoEventoTemp)) {
			return false;
		}
		ProcessoEventoTemp other = (ProcessoEventoTemp) obj;
		if (getIdProcessoEventoTemp() != other.getIdProcessoEventoTemp()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdProcessoEventoTemp();
		return result;
	}

}
