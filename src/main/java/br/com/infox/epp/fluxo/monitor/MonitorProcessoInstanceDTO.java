package br.com.infox.epp.fluxo.monitor;

import java.util.Date;

import org.jbpm.graph.exe.Token;

import br.com.infox.cdi.producer.EntityManagerProducer;

public class MonitorProcessoInstanceDTO {

    private String numero;
    private String nodeName;
    private Date dataInicio;
    private MonitorProcessoState state;
    private Token token;

    public MonitorProcessoInstanceDTO(String numero, String nodeName, Date dataInicio, String state, Long idToken) {
        this.numero = numero;
        this.nodeName = nodeName;
        this.dataInicio = dataInicio;
        this.state = MonitorProcessoState.valueOf(state);
        this.token = EntityManagerProducer.getEntityManager().find(Token.class, idToken);
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

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
