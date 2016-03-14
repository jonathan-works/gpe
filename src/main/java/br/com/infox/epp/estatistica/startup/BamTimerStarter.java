package br.com.infox.epp.estatistica.startup;

import java.util.Properties;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.async.QuartzDispatcher;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import org.jbpm.util.ClassLoaderUtil;
import org.quartz.SchedulerException;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.estatistica.abstracts.BamTimerProcessor;
import br.com.infox.epp.estatistica.manager.BamTimerManager;
import br.com.infox.epp.estatistica.processor.ProcessoTimerProcessor;
import br.com.infox.epp.estatistica.processor.TarefaTimerProcessor;
import br.com.infox.quartz.QuartzConstant;

@Name(BamTimerStarter.NAME)
@Scope(ScopeType.STATELESS)
@AutoCreate
public class BamTimerStarter {
    
    private static final LogProvider LOG = Logging.getLogProvider(BamTimerStarter.class);
    private static final Properties QUARTZ_PROPERTIES = ClassLoaderUtil.getProperties(QuartzConstant.QUARTZ_PROPERTIES);
    private static final String PROCESSO_CRON_EXPRESSION = "0 0 0 * * ?";
    private static final String TAREFA_CRON_EXPRESSION = "0 0/30 * * * ?";

    public static final String NAME = "bamTimerStarter";
    public static final String ID_INICIAR_PROCESSO_TIMER_PARAMETER = "idProcessoTimerParameter";
    public static final String ID_INICIAR_TASK_TIMER_PARAMETER = "idTaskTimerParameter";

    @Observer(value = QuartzDispatcher.QUARTZ_DISPATCHER_INITIALIZED_EVENT)
    @Transactional
    public void create() {
        if (!Boolean.parseBoolean(QUARTZ_PROPERTIES.getProperty(QuartzConstant.QUARTZ_TIMER_ENABLED))) {
            return;
        }

        BamTimerManager bamTimerManager = (BamTimerManager) Component.getInstance(BamTimerManager.NAME);
        initProcessoTimerProcessor(bamTimerManager);
        initTarefaTimerProcessor(bamTimerManager);
    }

    private void initTarefaTimerProcessor(final BamTimerManager bamTimerManager) {
        TarefaTimerProcessor processor = (TarefaTimerProcessor) Component.getInstance(TarefaTimerProcessor.NAME);
        String cronExpression = QUARTZ_PROPERTIES.getProperty(QuartzConstant.QUARTZ_CRON_EXPRESSION, TAREFA_CRON_EXPRESSION);

        initTimerProcessor(cronExpression, ID_INICIAR_TASK_TIMER_PARAMETER, "ID do timer de tarefas do sistema", processor, bamTimerManager);
    }

    private void initProcessoTimerProcessor(BamTimerManager bamTimerManager) {
        ProcessoTimerProcessor processor = (ProcessoTimerProcessor) Component.getInstance(ProcessoTimerProcessor.NAME);
        String cronExpression = QUARTZ_PROPERTIES.getProperty(QuartzConstant.QUARTZ_CRON_EXPRESSION, PROCESSO_CRON_EXPRESSION);

        initTimerProcessor(cronExpression, ID_INICIAR_PROCESSO_TIMER_PARAMETER, "ID do timer de projetos do sistema", processor, bamTimerManager);
    }

    private void initTimerProcessor(String cronExpression, String idTimer, String description, BamTimerProcessor processor, BamTimerManager bamTimerManager) {
        try {
            String idIniciarFluxoTimer = null;
            try {
                idIniciarFluxoTimer = bamTimerManager.getParametro(idTimer);
            } catch (IllegalArgumentException e) {
                LOG.error("TarefaTimerStarter.init()", e);
            }
            if (idIniciarFluxoTimer == null) {
                bamTimerManager.createTimerInstance(cronExpression, idTimer, description, processor);
            }
        } catch (SchedulerException | DAOException e) {
            LOG.error(".initTimerProcessor(cronExpression, idTimer, description, processor, bamTimerManager)", e);
        }
    }

}
