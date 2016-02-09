package br.com.infox.epp.processo.home;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.epp.quartz.client.QuartzRestFactory;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@AutoCreate
@Name(AutomaticNodeRetryProcessor.NAME)
public class AutomaticNodeRetryProcessor {
    
    public static final String NAME = "automaticNodeRetryProcessor";
    private static final LogProvider LOG = Logging.getLogProvider(AutomaticNodeRetryProcessor.class);
    
    @Asynchronous
    public QuartzTriggerHandle retryAutomaticNodes(@IntervalCron String cron) {
        try {
            QuartzRestFactory.create().retryAutomaticNodes();
        } catch (Exception e) {
            LOG.error(e);
        }
        return null;
    }
}
