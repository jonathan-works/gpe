package br.com.infox.quartz;

import java.io.Serializable;
import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public class TriggerListenerLog implements TriggerListener, Serializable {

    public static final String NAME = "TriggerListenerLog";
    private static final long serialVersionUID = 1L;
    private static final transient LogProvider LOG = Logging.getLogProvider(TriggerListenerLog.class);

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void triggerFired(Trigger trigger,
            JobExecutionContext executionContext) {
        JobDataMap jobDataMap = executionContext.getJobDetail().getJobDataMap();
        LOG.info("triggerFired: Job (" + trigger.getJobKey().getName() + ") / "
                + QuartzJobsInfo.getJobExpression(jobDataMap));
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        LOG.info("triggerMisfired: " + trigger.getJobKey().getName());
    }

    @Override
    public boolean vetoJobExecution(Trigger arg0, JobExecutionContext arg1) {
        return false;
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, CompletedExecutionInstruction triggerInstructionCode) {
        Date fireTime = context.getFireTime();
        long time = new Date().getTime() - fireTime.getTime();
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        LOG.info("triggerComplete: Job (" + trigger.getJobKey().getName() + ") / "
                + QuartzJobsInfo.getJobExpression(jobDataMap) + " [" + time
                + " ms]");
    }
}
