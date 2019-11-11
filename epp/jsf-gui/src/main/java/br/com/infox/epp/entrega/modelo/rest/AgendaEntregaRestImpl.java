package br.com.infox.epp.entrega.modelo.rest;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import br.com.infox.core.report.RequestInternalPageService;

public class AgendaEntregaRestImpl implements AgendaEntregaRest {

	@Inject 
	private AgendaEntregaTimerResourceImpl agendaEntregaTimerResourceImpl;
	@Inject 
    private RequestInternalPageService requestInternalPageService;
	
    @Override
    public AgendaEntregaTimerResource getQuartzResource(String key) {
        if (!requestInternalPageService.isValid(key)) {
            throw new WebApplicationException(401);
        }
        return agendaEntregaTimerResourceImpl;
    }

}
