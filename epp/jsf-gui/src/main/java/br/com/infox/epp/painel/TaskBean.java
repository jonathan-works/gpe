package br.com.infox.epp.painel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.painel.caixa.Caixa;
import lombok.Getter;
import lombok.Setter;

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
    private boolean exibirSelecaoAssinaturaLote;
    private boolean selecaoAssinaturaLote;
    @Getter @Setter
    private Boolean hasDocumentoParaAssinar;
    @Getter @Setter
    private boolean podeVisualizarProcesso;


    public TaskBean(String idTaskInstance, String taskName, String assignee, String idProcessInstance, String taskNodeKey,
            Integer idProcesso, String nomeCaixa, Integer idCaixa, String nomeNatureza,
            String nomeCategoria, String numeroProcesso, Integer idProcessoRoot, String numeroProcessoRoot, String nomeUsuarioSolicitante,
            Integer idPrioridadeProcesso, String prioridadeProcesso, Integer pesoPrioridadeProcesso, Date dataInicio,
            String nomeNaturezaProcessoRoot, String nomeCategoriaProcessoRoot, Boolean temDocumentoParaAssinar) {
        this.idTaskInstance = idTaskInstance;
        this.taskName = taskName;
        this.assignee = assignee;
        this.idProcessInstance = idProcessInstance;
        this.taskNodeKey = taskNodeKey;
        this.idProcesso = idProcesso;
        this.nomeCaixa = nomeCaixa;
        this.idCaixa = idCaixa;
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
        this.hasDocumentoParaAssinar = temDocumentoParaAssinar;
    }

    public TaskBean(Object[] record, boolean podeVisualizarProcesso) {
        this.idTaskInstance =  (String) record[0];
        this.taskName = (String)  record[1];
        this.assignee = (String) record[2];
        this.idProcessInstance =  (String) record[3];
        this.taskNodeKey =   (String) record[4];
        this.idProcesso =   validateIntValue(record[5]);
        this.nomeCaixa = (String) record[6];
        this.idCaixa = validateIntValue(record[7]);
        this.nomeNatureza =  (String)  record[8];
        this.nomeCategoria =   (String)  record[9];
        this.numeroProcesso =  (String)  record[10];
        this.idProcessoRoot = validateIntValue(record[11]);
        this.numeroProcessoRoot =  (String)  record[12];
        this.nomeUsuarioSolicitante =  (String)  record[13];
        this.idPrioridadeProcesso = validateIntValue(record[14]);
        this.prioridadeProcesso = (String) record[15];
        this.pesoPrioridadeProcesso = validateIntValue(record[16]) == null ? -1 : validateIntValue(record[16]);
        this.dataInicio = convertDate(record[17]);
        this.nomeNaturezaProcessoRoot = (String) record[18];
        this.nomeCategoriaProcessoRoot =  (String) record[19];;
        this.hasDocumentoParaAssinar = validateBooleanValue(record[20].toString());
        this.nomeFluxo =  (String) record[21];
        this.idFluxo = validateIntValue(record[22]);
        this.podeVisualizarProcesso = podeVisualizarProcesso;
    }

    private Date convertDate(Object value){
        return Date.from(LocalDateTime.parse(value.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()).toInstant());
    }

    private boolean validateBooleanValue(Object value){
        return value == null ? false : validateIntValue(value) == 1 ? true : false;
    }

    private Integer validateIntValue(Object value){
        return value == null ? null : Integer.valueOf(value.toString());
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

    public void setNomeFluxo(String nomeFluxo) {
        this.nomeFluxo = nomeFluxo;
    }

    public void setIdFluxo(Integer idFluxo) {
        this.idFluxo = idFluxo;
    }

	public boolean isExibirSelecaoAssinaturaLote() {
		return exibirSelecaoAssinaturaLote;
	}

	public void setExibirSelecaoAssinaturaLote(boolean exibirSelecaoAssinaturaLote) {
		this.exibirSelecaoAssinaturaLote = exibirSelecaoAssinaturaLote;
	}

	public boolean isSelecaoAssinaturaLote() {
		return selecaoAssinaturaLote;
	}

	public void setSelecaoAssinaturaLote(boolean selecaoAssinaturaLote) {
		this.selecaoAssinaturaLote = selecaoAssinaturaLote;
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
