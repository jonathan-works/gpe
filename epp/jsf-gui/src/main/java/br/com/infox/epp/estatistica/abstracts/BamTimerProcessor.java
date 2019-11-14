package br.com.infox.epp.estatistica.abstracts;

import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

public interface BamTimerProcessor {

    public abstract QuartzTriggerHandle increaseTimeSpent(@IntervalCron String cron);

}
