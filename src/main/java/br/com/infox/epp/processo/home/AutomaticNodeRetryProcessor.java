package br.com.infox.epp.processo.home;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.epp.quartz.client.QuartzRestFactory;

@AutoCreate
@Name(AutomaticNodeRetryProcessor.NAME)
public class AutomaticNodeRetryProcessor {
    
    public static final String NAME = "automaticNodeRetryProcessor";
    
    @Asynchronous
    public QuartzTriggerHandle retryAutomaticNodes(@IntervalCron String cron) {
        QuartzRestFactory.create().retryAutomaticNodes();
        return null;
    }
}
