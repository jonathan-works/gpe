package br.com.infox.epp.estatistica.startup;

import java.util.Properties;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Create;
import org.jbpm.util.ClassLoaderUtil;
import org.quartz.SchedulerException;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.estatistica.manager.BamTimerManager;
import br.com.infox.epp.estatistica.processor.ProcessoTimerProcessor;
import br.com.infox.quartz.QuartzConstant;

public class ProcessoTimerStarter {

	private static final String DEFAULT_CRON_EXPRESSION = "0 0 0 * * ?";
	private static final Properties QUARTZ_PROPERTIES = ClassLoaderUtil
			.getProperties(QuartzConstant.QUARTZ_PROPERTIES);
	public static final String NAME = "processoTimerStarter";
	public static final String ID_INICIAR_PROCESSO_TIMER_PARAMETER = "idProcessoTimerParameter";
	
	public ProcessoTimerStarter() {}

	@Create
	public void init() throws SchedulerException, DAOException {
		if (!Boolean.parseBoolean(QUARTZ_PROPERTIES
				.getProperty(QuartzConstant.QUARTZ_TIMER_ENABLED))) {
			return;
		}

		final BamTimerManager bamTimerManager = (BamTimerManager) Component.getInstance(BamTimerManager.NAME);
		final String property = QUARTZ_PROPERTIES.getProperty(
                QuartzConstant.QUARTZ_CRON_EXPRESSION,
                DEFAULT_CRON_EXPRESSION);
        final ProcessoTimerProcessor processor = (ProcessoTimerProcessor) Component.getInstance(ProcessoTimerProcessor.NAME);
        bamTimerManager.createTimerInstance(property,ID_INICIAR_PROCESSO_TIMER_PARAMETER,"ID do timer do sistema",processor);
	}

}