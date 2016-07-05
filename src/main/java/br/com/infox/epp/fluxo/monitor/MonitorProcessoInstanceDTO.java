package br.com.infox.epp.fluxo.monitor;

import java.util.Date;

public class MonitorProcessoInstanceDTO {

    private String numero;
    private String nodeName;
    private Date dataInicio;
    private MonitorProcessoState state;
    private Long tokenId;

    public MonitorProcessoInstanceDTO(String numero, String nodeName, Date dataInicio, String state, Long tokenId) {
        super();
        this.numero = numero;
        this.nodeName = nodeName;
        this.dataInicio = dataInicio;
        this.state = MonitorProcessoState.valueOf(state);
        this.tokenId = tokenId;
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

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }
}
