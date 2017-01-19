package br.com.infox.epp.fluxo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tb_historico_proc_def")
public class HistoricoProcessDefinition implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "HistoricoProcessDefinitionGenerator", allocationSize = 1, initialValue = 1, sequenceName = "sq_historico_proc_def")
	@GeneratedValue(generator = "HistoricoProcessDefinitionGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_historico_proc_def")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_definicao_processo", nullable = false)
	private DefinicaoProcesso definicaoProcesso;
	
	@NotNull
	@Column(name = "ds_process_definition", nullable = false)
	private String processDefinition;
	
	@Column(name = "ds_bpmn")
	private String bpmn;
	
	@Column(name = "ds_svg")
	private String svg;
	
	@NotNull
	@Column(name = "nr_revisao", nullable = false)
	private Integer revisao;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_alteracao", nullable = false)
	private Date dataAlteracao;

	@PrePersist
	private void prePersist() {
		dataAlteracao = new Date();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DefinicaoProcesso getDefinicaoProcesso() {
        return definicaoProcesso;
    }
	
	public void setDefinicaoProcesso(DefinicaoProcesso definicaoProcesso) {
        this.definicaoProcesso = definicaoProcesso;
    }

	public String getProcessDefinition() {
		return processDefinition;
	}

	public void setProcessDefinition(String processDefinition) {
		this.processDefinition = processDefinition;
	}
	
	public String getBpmn() {
		return bpmn;
	}
	
	public void setBpmn(String bpmn) {
		this.bpmn = bpmn;
	}
	
	public String getSvg() {
		return svg;
	}
	
	public void setSvg(String svg) {
		this.svg = svg;
	}

	public Integer getRevisao() {
		return revisao;
	}
	
	public void setRevisao(Integer revisao) {
		this.revisao = revisao;
	}
	
	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
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
		if (!(obj instanceof HistoricoProcessDefinition))
			return false;
		HistoricoProcessDefinition other = (HistoricoProcessDefinition) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
}
