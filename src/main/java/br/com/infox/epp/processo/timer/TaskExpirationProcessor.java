package br.com.infox.epp.processo.timer;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.epp.quartz.client.QuartzRestFactory;

@Name(TaskExpirationProcessor.NAME)
@AutoCreate
public class TaskExpirationProcessor {

    public static final String NAME = "taskExpirationProcessor";
    
    @Asynchronous
    public QuartzTriggerHandle processTaskExpiration(@IntervalCron String cron) {
        QuartzRestFactory.create().taskExpirationProcessor();
        return null;
    }
}
