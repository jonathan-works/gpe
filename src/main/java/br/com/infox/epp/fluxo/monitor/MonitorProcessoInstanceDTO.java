package br.com.infox.epp.fluxo.monitor;

import java.util.Date;

public class MonitorProcessoInstanceDTO {

    private String numero;
    private String nodeName;
    private Date dataInicio;
    private MonitorProcessoState state;

    public MonitorProcessoInstanceDTO(String numero, String nodeName, Date dataInicio, String state) {
        super();
        this.numero = numero;
        this.nodeName = nodeName;
        this.dataInicio = dataInicio;
        this.state = MonitorProcessoState.valueOf(state);
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
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

    public MonitorProcessoState getState() {
        return state;
    }

    public void setState(MonitorProcessoState state) {
        this.state = state;
    }
}
