package br.com.infox.epp.entrega.modelo.rest;

import javax.ws.rs.POST;

public interface AgendaEntregaTimerResource {

    @POST
    public void processAgendaEntrega();

}
