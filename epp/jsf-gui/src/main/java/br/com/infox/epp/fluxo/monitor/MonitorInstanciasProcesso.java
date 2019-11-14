package br.com.infox.epp.fluxo.monitor;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "vs_monitor_instancias_processo")
public class MonitorInstanciasProcesso implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_token", nullable = false, insertable = false, updatable = false)
    private Long idToken;
    
    @Column(name = "nr_processo", nullable = false, insertable = false, updatable = false)
    private String numeroProcesso;
    
    @Column(name = "nm_node", nullable = false, insertable = false, updatable = false)
    private String nodeName;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_inicio", nullable = false, insertable = false, updatable = false)
    private Date dataInicio;
    
    @Column(name = "ds_state", nullable = false, insertable = false, updatable = false)
    private String state;
    
    @Column(name = "cd_node_key", nullable = false, insertable = false, updatable = false)
    private String nodeKey;
    
    @Column(name = "id_process_definition", nullable = false, insertable = false, updatable = false)
    private Long idProcessDefinition;

    public Long getIdToken() {
        return idToken;
    }

    public void setIdToken(Long idToken) {
        this.idToken = idToken;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public Long getIdProcessDefinition() {
        return idProcessDefinition;
    }

    public void setIdProcessDefinition(Long idProcessDefinition) {
        this.idProcessDefinition = idProcessDefinition;
    }
}
