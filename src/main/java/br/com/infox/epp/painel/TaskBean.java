package br.com.infox.epp.painel;

import java.util.Date;

public class TaskBean {
    
    private Long idTaskInstance;
    private String taskName;
    private String assignee;
    private Long idProcessInstance;
    private String taskNodeKey;
    private Integer idProcesso;
    private String nomeCaixa;
    private Integer idCaixa;
    private String nomeFluxo;
    private Integer idFluxo;
    private String nomeNatureza;
    private String nomeCategoria;
    private String numeroProcesso;
    private String numeroProcessoRoot;
    private String nomeUsuarioSolicitante;
    private String prioridadeProcesso;
    private Integer pesoPrioridadeProcesso;
    private Date dataInicio;
    
    
    public TaskBean(Long idTaskInstance, String taskName, String assignee, Long idProcessInstance, String taskNodeKey,
            Integer idProcesso, String nomeCaixa, Integer idCaixa, String nomeFluxo, Integer idFluxo, String nomeNatureza, 
            String nomeCategoria, String numeroProcesso, String numeroProcessoRoot, String nomeUsuarioSolicitante, 
            String prioridadeProcesso, Integer pesoPrioridadeProcesso, Date dataInicio) {
        this.idTaskInstance = idTaskInstance;
        this.taskName = taskName;
        this.assignee = assignee;
        this.idProcessInstance = idProcessInstance;
        this.taskNodeKey = taskNodeKey;
        this.idProcesso = idProcesso;
        this.nomeCaixa = nomeCaixa;
        this.idCaixa = idCaixa;
        this.nomeFluxo = nomeFluxo;
        this.idFluxo = idFluxo;
        this.nomeNatureza = nomeNatureza;
        this.nomeCategoria = nomeCategoria;
        this.numeroProcesso = numeroProcesso;
        this.numeroProcessoRoot = numeroProcessoRoot;
        this.nomeUsuarioSolicitante = nomeUsuarioSolicitante;
        this.prioridadeProcesso = prioridadeProcesso;
        this.pesoPrioridadeProcesso = pesoPrioridadeProcesso == null ? -1 : pesoPrioridadeProcesso;
        this.dataInicio = dataInicio;
    }
    
    public Long getIdTaskInstance() {
        return idTaskInstance;
    }
    
    public String getTaskName() {
        return taskName;
    }
    
    public String getAssignee() {
        return assignee;
    }
    
    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Long getIdProcessInstance() {
        return idProcessInstance;
    }
    
    public String getTaskNodeKey() {
        return taskNodeKey;
    }
    
    public Integer getIdProcesso() {
        return idProcesso;
    }
    
    public String getNomeCaixa() {
        return nomeCaixa;
    }
    
    public Integer getIdCaixa() {
        return idCaixa;
    }
    
    public String getNomeFluxo() {
        return nomeFluxo;
    }
    
    public Integer getIdFluxo() {
        return idFluxo;
    }

    public String getNomeNatureza() {
        return nomeNatureza;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public String getNumeroProcessoRoot() {
        return numeroProcessoRoot;
    }

    public String getNomeUsuarioSolicitante() {
        return nomeUsuarioSolicitante;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public String getPrioridadeProcesso() {
        return prioridadeProcesso;
    }
    
    public Integer getPesoPrioridadeProcesso() {
        return pesoPrioridadeProcesso;
    }

    @Override
    public String toString() {
        return "TaskBean [idTaskInstance=" + idTaskInstance + ", taskName=" + taskName + ", assignee=" + assignee
                + ", idProcessInstance=" + idProcessInstance + ", taskNodeKey=" + taskNodeKey + ", idProcesso="
                + idProcesso + ", nomeCaixa=" + nomeCaixa + ", idCaixa=" + idCaixa + ", nomeFluxo=" + nomeFluxo
                + ", idFluxo=" + idFluxo + ", nomeNatureza=" + nomeNatureza + ", nomeCategoria=" + nomeCategoria
                + ", numeroProcesso=" + numeroProcesso + ", numeroProcessoRoot=" + numeroProcessoRoot
                + ", nomeUsuarioSolicitante=" + nomeUsuarioSolicitante + ", prioridadeProcesso=" + prioridadeProcesso
                + ", dataInicio=" + dataInicio + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idTaskInstance == null) ? 0 : idTaskInstance.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof TaskBean))
            return false;
        TaskBean other = (TaskBean) obj;
        if (idTaskInstance == null) {
            if (other.idTaskInstance != null)
                return false;
        } else if (!idTaskInstance.equals(other.idTaskInstance))
            return false;
        return true;
    }
    
}
