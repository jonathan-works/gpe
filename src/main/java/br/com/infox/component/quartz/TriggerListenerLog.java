package br.com.infox.component.quartz;

import java.io.Serializable;
import java.util.Date;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

public class TriggerListenerLog implements TriggerListener, Serializable {
	
	public static final String NAME = "TriggerListenerLog";
	private static final long serialVersionUID = 1L;
	private static transient final LogProvider log = Logging.getLogProvider(TriggerListenerLog.class);

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void triggerComplete(Trigger trigger, JobExecutionContext executionContext, int arg2) {
		Date fireTime = executionContext.getFireTime();
		long time = new Date().getTime() - fireTime.getTime();
		JobDataMap jobDataMap = executionContext.getJobDetail().getJobDataMap();
		log.info("triggerComplete: Job (" + trigger.getJobName() + ") / " + QuartzJobsInfo.getJobExpression(jobDataMap) + " [" + time + " ms]");
	}

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext executionContext) {
		JobDataMap jobDataMap = executionContext.getJobDetail().getJobDataMap();
		log.info("triggerFired: Job (" + trigger.getJobName() + ") / " + QuartzJobsInfo.getJobExpression(jobDataMap));
	}

	@Override
	public void triggerMisfired(Trigger trigger) {
		log.info("triggerMisfired: " + trigger.getName());
	}

	@Override
	public boolean vetoJobExecution(Trigger arg0, JobExecutionContext arg1) {
		return false;
	}
	
}