package br.com.infox.epp.processo.timer;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.epp.quartz.client.QuartzRestFactory;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(TaskExpirationProcessor.NAME)
@AutoCreate
public class TaskExpirationProcessor {

    public static final String NAME = "taskExpirationProcessor";
    private static final LogProvider LOG = Logging.getLogProvider(TaskExpirationProcessor.class);
    
    @Asynchronous
    public QuartzTriggerHandle processTaskExpiration(@IntervalCron String cron) {
        try {
            QuartzRestFactory.create().taskExpirationProcessor();
        } catch (Exception e) {
            LOG.error(e);
        }
        return null;
    }
}
