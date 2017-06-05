package br.com.infox.epp.entrega.modelo.quartz;

import javax.inject.Inject;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.core.report.RequestInternalPageService;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.entrega.modelo.rest.AgendaEntregaRest;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.ws.factory.RestClientFactory;

@ContextDependency
@AutoCreate
@Name(AgendaEntregaTimerProcessor.NAME)
public class AgendaEntregaTimerProcessor {
    
    @Inject
    protected ParametroManager parametroManager;
    
    static final String NAME = "agendaEntregaTimerProcessor";

    @Asynchronous
    public QuartzTriggerHandle processAgendaEntregaSync(@IntervalCron String cron) {
        
        if (parametroManager.isNaoExecutarQuartz()) {
            return null;
        }
        
        String key = getKey();
        RestClientFactory.create(getUrl(), AgendaEntregaRest.class).getQuartzResource(key).processAgendaEntrega();
        return null;
    }

    private static String getUrl() {
        return getRequestInternalPageService().getResquestUrlRest();
    }

    private static String getKey() {
        return getRequestInternalPageService().getKey().toString();
    }

    private static RequestInternalPageService getRequestInternalPageService() {
        return BeanManager.INSTANCE.getReference(RequestInternalPageService.class);
    }

}
