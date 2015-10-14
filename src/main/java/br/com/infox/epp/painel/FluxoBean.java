package br.com.infox.epp.painel;

import br.com.infox.epp.processo.type.TipoProcesso;

public class FluxoBean {
	
	private String processDefinitionId;
	private String name;
	private Long quantidadeProcessos;
	private TipoProcesso tipoProcesso;
	private Boolean expedida = false;
	private boolean bpmn20;
	
	public FluxoBean() {
	}

	public FluxoBean(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public FluxoBean(String processDefinitionId, Boolean expedida) {
		this(processDefinitionId);
		this.expedida = expedida;
	}
	
	public FluxoBean(String processDefinitionId, String name, boolean bpmn20) {
		this.processDefinitionId = processDefinitionId;
		this.name = name;
		this.bpmn20 = bpmn20;
	}
	
	public FluxoBean(String name, Long quantidadeProcessos) {
		this.name = name;
		this.quantidadeProcessos = quantidadeProcessos;
	}
	
	public FluxoBean(String processDefinitionId, String name, Long quantidadeProcessos) {
		this(name, quantidadeProcessos);
		this.processDefinitionId = processDefinitionId;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Long getQuantidadeProcessos() {
		return quantidadeProcessos;
	}

	public void setQuantidadeProcessos(Long quantidadeProcessos) {
		this.quantidadeProcessos = quantidadeProcessos;
	}

	public TipoProcesso getTipoProcesso() {
		return tipoProcesso;
	}

	public void setTipoProcesso(TipoProcesso tipoProcesso) {
		this.tipoProcesso = tipoProcesso;
	}
	
	public Boolean getExpedida() {
		return expedida;
	}

	public void setExpedida(Boolean expedida) {
		this.expedida = expedida;
	}

	public boolean isBpmn20() {
		return bpmn20;
	}
	
	public void setBpmn20(boolean bpmn20) {
		this.bpmn20 = bpmn20;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expedida == null) ? 0 : expedida.hashCode());
		result = prime * result + ((processDefinitionId == null) ? 0 : processDefinitionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FluxoBean))
			return false;
		FluxoBean other = (FluxoBean) obj;
		if (expedida == null) {
			if (other.expedida != null)
				return false;
		} else if (!expedida.equals(other.expedida))
			return false;
		if (processDefinitionId == null) {
			if (other.processDefinitionId != null)
				return false;
		} else if (!processDefinitionId.equals(other.processDefinitionId))
			return false;
		return true;
	}

}
