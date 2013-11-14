package br.com.infox.epp.service.startup;

import java.util.Date;
import java.util.Properties;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.util.ClassLoaderUtil;
import org.quartz.SchedulerException;

import br.com.infox.component.quartz.QuartzConstant;
import br.com.infox.epp.estatistica.processor.TarefaTimerProcessor;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.timer.TimerUtil;
import br.com.itx.util.EntityUtil;

@Name(TarefaTimerStarter.NAME)
@Scope(ScopeType.APPLICATION)
@Startup(depends = QuartzConstant.JBOSS_SEAM_ASYNC_DISPATCHER)
@Install(dependencies = { QuartzConstant.JBOSS_SEAM_ASYNC_DISPATCHER })
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
	@Transactional
	public void init() throws SchedulerException {
		if (!Boolean.parseBoolean(QUARTZ_PROPERTIES
				.getProperty(QuartzConstant.QUARTZ_TIMER_ENABLED))) {
			return;
		}

		String idIniciarFluxoTimer = null;
		try {
			idIniciarFluxoTimer = TimerUtil
					.getParametro(ID_INICIAR_TASK_TIMER_PARAMETER);
		} catch (IllegalArgumentException e) {
			LOG.error("TarefaTimerStarter.init()", e);
		}
		if (idIniciarFluxoTimer == null) {
			Parametro p = new Parametro();
			p.setAtivo(true);
			p.setDescricaoVariavel("ID do timer do sistema");
			p.setDataAtualizacao(new Date());
			p.setNomeVariavel(ID_INICIAR_TASK_TIMER_PARAMETER);
			p.setSistema(true);

			String cronExpression = QUARTZ_PROPERTIES.getProperty(
					QuartzConstant.QUARTZ_CRON_EXPRESSION,
					DEFAULT_CRON_EXPRESSION);

			TarefaTimerProcessor processor = TarefaTimerProcessor.instance();
			QuartzTriggerHandle handle = processor
					.increaseTaskTimeSpent(cronExpression);
			EntityUtil.getEntityManager().flush();
			String triggerName = handle.getTrigger().getName();
			p.setValorVariavel(triggerName);
			EntityUtil.getEntityManager().persist(p);
			EntityUtil.getEntityManager().flush();
		}
	}

}