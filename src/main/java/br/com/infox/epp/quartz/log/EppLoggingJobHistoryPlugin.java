package br.com.infox.epp.quartz.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.plugins.history.LoggingJobHistoryPlugin;

import br.com.infox.core.server.ApplicationServerService;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.quartz.QuartzJobsInfo;

public class EppLoggingJobHistoryPlugin extends LoggingJobHistoryPlugin {

	private static final LogProvider LOG = Logging.getLogProvider(EppLoggingJobHistoryPlugin.class);
	private static final String jobToBeFiredMessage = "Job {0} disparado (pela trigger {3}) \u00E0s: {2, date, HH:mm:ss dd/MM/yyyy}";
	private static final String jobSuccessMessage = "Job {0} completou a execu\u00E7\u00E3o \u00E0s {2, date, HH:mm:ss dd/MM/yyyy}";
	private static final String jobFailedMessage = "Job {0} falhou na execu\u00E7\u00E3o \u00E0s {2, date, HH:mm:ss dd/MM/yyyy} e registrou: {8}";
	private static final String jobWasVetoedMessage = "Job {0} foi vetado.  Seria disparado (pela trigger {3}) \u00E0s: {2, date, HH:mm:ss dd/MM/yyyy}";

	private ThreadLocal<Long> idLogEntry = new ThreadLocal<Long>();
	
	@Override
	public String getJobToBeFiredMessage() {
		return jobToBeFiredMessage;
	}

	@Override
	public String getJobSuccessMessage() {
		return jobSuccessMessage;
	}

	@Override
	public String getJobFailedMessage() {
		return jobFailedMessage;
	}

	@Override
	public String getJobWasVetoedMessage() {
		return jobWasVetoedMessage;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		super.jobToBeExecuted(context);
		
		LogQuartz logEntry = new LogQuartz();
		logEntry.setJobName(context.getJobDetail().getKey().getName());
		logEntry.setTriggerName(context.getTrigger().getKey().getName());
		logEntry.setDataInicioProcessamento(context.getFireTime());
		logEntry.setInstancia(ApplicationServerService.instance().getInstanceName());
		logEntry.setExpressao(QuartzJobsInfo.getJobExpression(context.getJobDetail().getJobDataMap()));
		getLogQuartzDao().inserir(logEntry);
		idLogEntry.set(logEntry.getId());
	}
	
	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		super.jobWasExecuted(context, jobException);
		
		if (idLogEntry.get() > 0) {
			LogQuartz logEntry = getLogQuartzDao().findById(idLogEntry.get());
			try {
				if (context.getJobRunTime() != -1) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(context.getFireTime());
					calendar.add(Calendar.MILLISECOND, (int) context.getJobRunTime());
					logEntry.setDataFimProcessamento(calendar.getTime());
				}
			} catch (Exception e) {
				LOG.error("Erro ao gravar data fim da execução do log do quartz", e);
			}
			if (jobException != null) {
				StringWriter stringWriter = new StringWriter();
				jobException.printStackTrace(new PrintWriter(stringWriter));
				logEntry.setExcecaoStackTrace(stringWriter.toString());
			}
			getLogQuartzDao().atualizar(logEntry);
		}
	}
	
	private LogQuartzDao getLogQuartzDao() {
		return BeanManager.INSTANCE.getReference(LogQuartzDao.class);
	}
}
