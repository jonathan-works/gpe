package br.com.infox.epp.fluxo.monitor;

import java.util.List;

import org.jbpm.graph.def.ProcessDefinition;

import br.com.infox.epp.fluxo.entity.Fluxo;

public class MonitorProcessoDTO {

    private Fluxo fluxo;
    private ProcessDefinition processDefinition;
    private List<MonitorTarefaDTO> humanNodeList;
    private List<MonitorTarefaDTO> automaticNodeList;
    private String svg;

    public MonitorProcessoDTO(Fluxo f, ProcessDefinition pd, List<MonitorTarefaDTO> hnList, List<MonitorTarefaDTO> anList, String svg) {
        super();
        this.fluxo = f;
        this.processDefinition = pd;
        this.humanNodeList = hnList;
        this.automaticNodeList = anList;
        this.svg = svg;
    }

    public Fluxo getFluxo() {
        return fluxo;
    }

    public void setFluxo(Fluxo fluxo) {
        this.fluxo = fluxo;
    }

    public ProcessDefinition getProcessDefinition() {
        return processDefinition;
    }

    public void setProcessDefinition(ProcessDefinition processDefinition) {
        this.processDefinition = processDefinition;
    }

    public List<MonitorTarefaDTO> getHumanNodeList() {
        return humanNodeList;
    }

    public void setHumanNodeList(List<MonitorTarefaDTO> humanNodeList) {
        this.humanNodeList = humanNodeList;
    }

    public List<MonitorTarefaDTO> getAutomaticNodeList() {
        return automaticNodeList;
    }

    public void setAutomaticNodeList(List<MonitorTarefaDTO> automaticNodeList) {
        this.automaticNodeList = automaticNodeList;
    }

    public String getSvg() {
        return svg;
    }

    public void setSvg(String svg) {
        this.svg = svg;
    }
}
