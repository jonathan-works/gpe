package br.com.infox.epp.entrega.modelo.quartz;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.core.report.RequestInternalPageService;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.entrega.modelo.rest.AgendaEntregaRest;
import br.com.infox.ws.factory.RestClientFactory;

@AutoCreate
@Name(AgendaEntregaTimerProcessor.NAME)
public class AgendaEntregaTimerProcessor {
    static final String NAME = "agendaEntregaTimerProcessor";

    @Asynchronous
    public QuartzTriggerHandle processAgendaEntregaSync(@IntervalCron String cron) {
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
