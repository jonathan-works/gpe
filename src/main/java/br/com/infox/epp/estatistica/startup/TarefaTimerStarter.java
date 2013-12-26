package br.com.infox.epp.estatistica.startup;

import java.util.Properties;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.util.ClassLoaderUtil;
import org.quartz.SchedulerException;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.estatistica.manager.BamTimerManager;
import br.com.infox.epp.estatistica.processor.TarefaTimerProcessor;
import br.com.infox.quartz.QuartzConstant;

public class TarefaTimerStarter {

	private static final String DEFAULT_CRON_EXPRESSION = "0 0/30 * * * ?";
	private static final LogProvider LOG = Logging
			.getLogProvider(TarefaTimerStarter.class);
	public static final String NAME = "tarefaTimerStarter";

	public static final String ID_INICIAR_TASK_TIMER_PARAMETER = "idTaskTimerParameter";
	private static final Properties QUARTZ_PROPERTIES = ClassLoaderUtil
			.getProperties(QuartzConstant.QUARTZ_PROPERTIES);

	public TarefaTimerStarter() {}

	@Create
	public void init() throws SchedulerException, DAOException {
		if (!Boolean.parseBoolean(QUARTZ_PROPERTIES
				.getProperty(QuartzConstant.QUARTZ_TIMER_ENABLED))) {
			return;
		}

		String idIniciarFluxoTimer = null;
		final BamTimerManager bamTimerManager = (BamTimerManager) Component.getInstance(BamTimerManager.NAME);
		try {
			idIniciarFluxoTimer = bamTimerManager.getParametro(ID_INICIAR_TASK_TIMER_PARAMETER);
		} catch (IllegalArgumentException e) {
			LOG.error("TarefaTimerStarter.init()", e);
		}
		if (idIniciarFluxoTimer == null) {
		    final String cronExpression = QUARTZ_PROPERTIES.getProperty(
	                QuartzConstant.QUARTZ_CRON_EXPRESSION,
	                DEFAULT_CRON_EXPRESSION);
		    final TarefaTimerProcessor processor = (TarefaTimerProcessor) Component.getInstance(TarefaTimerProcessor.NAME);
		    
		    bamTimerManager.createTimerInstance(cronExpression, ID_INICIAR_TASK_TIMER_PARAMETER, "ID do timer do sistema", processor);
		}
	}

}