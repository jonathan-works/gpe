package br.com.infox.epp.entrega.modelo.rest;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

@Path("/agendaEntrega")
public interface AgendaEntregaRest {

	@Path("/quartz")
    public AgendaEntregaTimerResource getQuartzResource(@HeaderParam("key") String key);
}
