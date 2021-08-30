package br.com.infox.epp.painel;

import java.util.Date;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.painel.caixa.Caixa;

public class TaskBean {

    private String idTaskInstance;
    private String taskName;
    private String assignee;
    private String idProcessInstance;
    private String taskNodeKey;
    private Integer idProcesso;
    private String nomeCaixa;
    private Integer idCaixa;
    private String nomeFluxo;
    private Integer idFluxo;
    private String nomeNatureza;
    private String nomeCategoria;
    private String numeroProcesso;
    private Integer idProcessoRoot;
    private String numeroProcessoRoot;
    private String nomeNaturezaProcessoRoot;
    private String nomeCategoriaProcessoRoot;
    private String nomeUsuarioSolicitante;
    private Integer idPrioridadeProcesso;
    private String prioridadeProcesso;
    private Integer pesoPrioridadeProcesso;
    private Date dataInicio;
    private String nomeUsuarioTarefa;


    public TaskBean(String idTaskInstance, String taskName, String assignee, String idProcessInstance, String taskNodeKey,
            Integer idProcesso, String nomeCaixa, Integer idCaixa, String nomeFluxo, Integer idFluxo, String nomeNatureza,
            String nomeCategoria, String numeroProcesso, Integer idProcessoRoot, String numeroProcessoRoot, String nomeUsuarioSolicitante,
            Integer idPrioridadeProcesso, String prioridadeProcesso, Integer pesoPrioridadeProcesso, Date dataInicio,
            String nomeNaturezaProcessoRoot, String nomeCategoriaProcessoRoot) {
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
        this.idProcessoRoot = idProcessoRoot;
        this.numeroProcessoRoot = numeroProcessoRoot;
        this.nomeNaturezaProcessoRoot = nomeNaturezaProcessoRoot;
        this.nomeCategoriaProcessoRoot = nomeCategoriaProcessoRoot;
        this.nomeUsuarioSolicitante = nomeUsuarioSolicitante;
        this.idPrioridadeProcesso = idPrioridadeProcesso;
        this.prioridadeProcesso = prioridadeProcesso;
        this.pesoPrioridadeProcesso = pesoPrioridadeProcesso == null ? -1 : pesoPrioridadeProcesso;
        this.dataInicio = dataInicio;
    }

    public String getIdTaskInstance() {
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

    public String getIdProcessInstance() {
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

    public Integer getIdProcessoRoot() {
        return idProcessoRoot;
    }

    public String getNumeroProcessoRoot() {
        return numeroProcessoRoot;
    }

    public String getNomeNaturezaProcessoRoot() {
        return nomeNaturezaProcessoRoot;
    }

    public String getNomeCategoriaProcessoRoot() {
        return nomeCategoriaProcessoRoot;
    }

    public String getNomeUsuarioSolicitante() {
        return nomeUsuarioSolicitante;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public Integer getIdPrioridadeProcesso() {
        return idPrioridadeProcesso;
    }

    public String getPrioridadeProcesso() {
        return prioridadeProcesso;
    }

    public Integer getPesoPrioridadeProcesso() {
        return pesoPrioridadeProcesso;
    }

    public String getNomeUsuarioTarefa() {
        if (nomeUsuarioTarefa == null && assignee != null && !Authenticator.getUsuarioLogado().getLogin().equals(assignee)) {
            nomeUsuarioTarefa = Beans.getReference(UsuarioLoginDAO.class).getUsuarioLoginByLogin(assignee).getNomeUsuario();
        }
        return nomeUsuarioTarefa;
    }

    public void removerCaixa() {
        this.idCaixa = null;
        this.nomeCaixa = null;
    }

    public void moverParaCaixa(Caixa caixa) {
        this.idCaixa = caixa.getIdCaixa();
        this.nomeCaixa = caixa.getNomeCaixa();
    }

    @Override
    public String toString() {
        return "TaskBean [idTaskInstance=" + idTaskInstance + ", taskName=" + taskName + ", assignee=" + assignee + ", idProcessInstance="
                + idProcessInstance + ", taskNodeKey=" + taskNodeKey + ", idProcesso=" + idProcesso + ", nomeCaixa=" + nomeCaixa + ", idCaixa="
                + idCaixa + ", nomeFluxo=" + nomeFluxo + ", idFluxo=" + idFluxo + ", nomeNatureza=" + nomeNatureza + ", nomeCategoria="
                + nomeCategoria + ", numeroProcesso=" + numeroProcesso + ", idProcessoRoot=" + idProcessoRoot + ", numeroProcessoRoot="
                + numeroProcessoRoot + ", nomeNaturezaProcessoRoot=" + nomeNaturezaProcessoRoot + ", nomeCategoriaProcessoRoot="
                + nomeCategoriaProcessoRoot + ", nomeUsuarioSolicitante=" + nomeUsuarioSolicitante + ", idPrioridadeProcesso=" + idPrioridadeProcesso
                + ", prioridadeProcesso=" + prioridadeProcesso + ", pesoPrioridadeProcesso=" + pesoPrioridadeProcesso + ", dataInicio=" + dataInicio
                + ", nomeUsuarioTarefa=" + nomeUsuarioTarefa + "]";
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
