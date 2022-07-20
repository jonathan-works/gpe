package br.com.infox.epp.painel;

import java.math.BigInteger;
import java.util.*;

import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.processo.type.TipoProcesso;

public class FluxoBean implements Comparable<FluxoBean> {

	private String processDefinitionId;
	private String name;
	private Long quantidadeProcessos;
	private TipoProcesso tipoProcesso;
	private Boolean expedida = false;
	private boolean bpmn20;
	private boolean podeVisualizarComExcessao;
	private List<Long> taskInstancesExcessao;

	private String numeroProcessoRootFilter;
	private Map<String, TaskBean> tasks;
	private Map<String, TaskDefinitionBean> taskDefinitions = new HashMap<>();

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

	public FluxoBean(String processDefinitionId, String name, Long quantidadeProcessos, String tipoProcesso, String expedida, String numeroProcessoRootFilter) {
        this.processDefinitionId = processDefinitionId;
        this.quantidadeProcessos = quantidadeProcessos;
        if (tipoProcesso != null) {
            this.tipoProcesso = TipoProcesso.getByName(tipoProcesso);
        }
        this.expedida = Boolean.valueOf(expedida);
        this.bpmn20 = false;
        this.numeroProcessoRootFilter = numeroProcessoRootFilter;

        if(TipoProcesso.COMUNICACAO.value().equals(tipoProcesso)) {
        	this.name = name + (this.expedida ? "-Expedidas" : "-Recebidas");
        }
        else
        {
            this.name = name;        	
        }
    }

	public FluxoBean(Object[] record, String tipoProcesso, String expedida, String numeroProcessoRootFilter) {
		podeVisualizarComExcessao = true;
		this.processDefinitionId =  BigInteger.valueOf(Long.valueOf(record[1].toString())).toString();
		this.quantidadeProcessos = validateIntValue(record[2]);
		if (tipoProcesso != null) {
			this.tipoProcesso = TipoProcesso.getByName(tipoProcesso);
		}
		this.expedida = Boolean.valueOf(expedida);
		this.bpmn20 = false;
		this.numeroProcessoRootFilter = numeroProcessoRootFilter;

		if(TipoProcesso.COMUNICACAO.value().equals(tipoProcesso)) {
			this.name = (String) record[0] + (this.expedida ? "-Expedidas" : "-Recebidas");
		}
		else
		{
			this.name = (String) record[0];
		}
		taskInstancesExcessao = new ArrayList<>();
		String[] split = record[3].toString().split(",");
		Arrays.asList(split).forEach(v -> {
			if (!StringUtil.isEmpty(v))
				taskInstancesExcessao.add(Long.valueOf(v));
		});
	}

	private Long validateIntValue(Object value){
		return value == null ? null : Long.valueOf(value.toString());
	}

	public void addTaskDefinition(TaskBean taskBean) {
        if (tasks == null) tasks = new HashMap<>();
		String taskNodeKey = taskBean.getTaskNodeKey();
		String taskName = taskBean.getTaskName();
		TaskDefinitionBean taskDefinitionBean = taskDefinitions.get(taskNodeKey);
		if (taskDefinitionBean == null) {
			taskDefinitionBean = new TaskDefinitionBean(taskNodeKey, taskName);
			taskDefinitions.put(taskNodeKey, taskDefinitionBean);
		}
		taskDefinitionBean.addTaskBean(taskBean);
		tasks.put(taskBean.getIdTaskInstance().toString(), taskBean);
	}

	public Map<String, TaskDefinitionBean> getTaskDefinitions() {
		return taskDefinitions;
	}

	public Map<String, TaskBean> getTasks() {
	    return tasks;
	}
	
	public TaskBean getTask(String idTaskInstance) {
		return tasks.get(idTaskInstance);
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

	public String getNumeroProcessoRootFilter() {
        return numeroProcessoRootFilter;
    }
	
	public void setNumeroProcessoRootFilter(String numeroProcessoRootFilter) {
        this.numeroProcessoRootFilter = numeroProcessoRootFilter;
    }

    @Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (bpmn20 ? 1231 : 1237);
		result = prime * result + ((expedida == null) ? 0 : expedida.hashCode());
		result = prime * result + ((processDefinitionId == null) ? 0 : processDefinitionId.hashCode());
		result = prime * result + ((quantidadeProcessos == null) ? 0 : quantidadeProcessos.hashCode());
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
		if (bpmn20 != other.bpmn20)
			return false;
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
		if (quantidadeProcessos == null) {
			if (other.quantidadeProcessos != null)
				return false;
		} else if (!quantidadeProcessos.equals(other.quantidadeProcessos))
			return false;
		return true;
	}

	@Override
	public int compareTo(FluxoBean o) {
		return this.getName().compareTo(o.getName());
	}

	public boolean isPodeVisualizarComExcessao() {
		return podeVisualizarComExcessao;
	}

	public List<Long> getTaskInstancesExcessao() {
		return taskInstancesExcessao;
	}
}
