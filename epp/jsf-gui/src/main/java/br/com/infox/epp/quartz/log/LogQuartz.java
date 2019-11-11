package br.com.infox.epp.quartz.log;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.system.annotation.Ignore;

@Ignore
@Entity
@Table(name = "tb_log_quartz")
public class LogQuartz implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "LogQuartzGenerator", sequenceName = "sq_log_quartz")
    @GeneratedValue(generator = "LogQuartzGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_log_quartz", nullable = false, unique = true)
    private Long id;

	@NotNull
	@Size(min = 1, max = 200)
	@Column(name = "nm_trigger_name", nullable = false)
	private String triggerName;
	
	@NotNull
	@Size(min = 1, max = 200)
	@Column(name = "nm_job_name", nullable = false)
	private String jobName;
	
	@NotNull
	@Column(name = "dt_inicio_processamento", nullable = false)
	private Date dataInicioProcessamento;
	
	@Column(name = "dt_fim_processamento")
	private Date dataFimProcessamento;
	
	@Size(max = 200)
	@Column(name = "nm_instancia")
	private String instancia;
	
	@Column(name = "ds_expressao")
	private String expressao;
	
	@Column(name = "ds_excecao_stacktrace")
	private String excecaoStackTrace;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Date getDataInicioProcessamento() {
		return dataInicioProcessamento;
	}

	public void setDataInicioProcessamento(Date dataInicioProcessamento) {
		this.dataInicioProcessamento = dataInicioProcessamento;
	}

	public Date getDataFimProcessamento() {
		return dataFimProcessamento;
	}

	public void setDataFimProcessamento(Date dataFimProcessamento) {
		this.dataFimProcessamento = dataFimProcessamento;
	}

	public String getInstancia() {
		return instancia;
	}

	public void setInstancia(String instancia) {
		this.instancia = instancia;
	}

	public String getExpressao() {
		return expressao;
	}

	public void setExpressao(String expressao) {
		this.expressao = expressao;
	}

	public String getExcecaoStackTrace() {
		return excecaoStackTrace;
	}

	public void setExcecaoStackTrace(String excecaoStackTrace) {
		this.excecaoStackTrace = excecaoStackTrace;
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
		if (!(obj instanceof LogQuartz))
			return false;
		LogQuartz other = (LogQuartz) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else {
			return !getId().equals(other.getId());
		}
		return false;
	}
}
