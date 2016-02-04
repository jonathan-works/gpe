package br.com.infox.epp.processo.timer;

import javax.inject.Inject;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.core.report.RequestInternalPageService;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.quartz.ws.QuartzRest;
import br.com.infox.epp.ws.client.RestClient;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(TaskExpirationProcessor.NAME)
@AutoCreate
@ContextDependency
public class TaskExpirationProcessor {

    public static final String NAME = "taskExpirationProcessor";
    private static final LogProvider LOG = Logging.getLogProvider(TaskExpirationProcessor.class);
    
    @Inject
    private RequestInternalPageService requestInternalPageService;

    @Asynchronous
    public QuartzTriggerHandle processTaskExpiration(@IntervalCron String cron) {
        try {
            QuartzRest quartzRest = RestClient.constructInternal(QuartzRest.class);
            quartzRest.taskExpirationProcessor(requestInternalPageService.getKey().toString());
        } catch (Exception e) {
            LOG.error(e);
        }
        return null;
    }
}
