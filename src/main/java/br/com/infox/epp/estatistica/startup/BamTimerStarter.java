package br.com.infox.epp.estatistica.startup;

import java.util.Properties;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.util.ClassLoaderUtil;
import org.quartz.SchedulerException;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.estatistica.abstracts.BamTimerProcessor;
import br.com.infox.epp.estatistica.manager.BamTimerManager;
import br.com.infox.epp.estatistica.processor.ProcessoTimerProcessor;
import br.com.infox.epp.estatistica.processor.TarefaTimerProcessor;
import br.com.infox.quartz.QuartzConstant;

@Name(BamTimerStarter.NAME)
@Scope(ScopeType.APPLICATION)
@Startup(depends = QuartzConstant.JBOSS_SEAM_ASYNC_DISPATCHER)
@Install(dependencies = { QuartzConstant.JBOSS_SEAM_ASYNC_DISPATCHER })
public class BamTimerStarter {
    private static final LogProvider LOG = Logging
            .getLogProvider(BamTimerStarter.class);
    private static final Properties QUARTZ_PROPERTIES = ClassLoaderUtil
            .getProperties(QuartzConstant.QUARTZ_PROPERTIES);
    private static final String PROCESSO_CRON_EXPRESSION = "0 0 0 * * ?";
    private static final String TAREFA_CRON_EXPRESSION = "0 0/30 * * * ?";
    
    public static final String NAME = "bamTimerStarter";
    public static final String ID_INICIAR_PROCESSO_TIMER_PARAMETER = "idProcessoTimerParameter";
    public static final String ID_INICIAR_TASK_TIMER_PARAMETER = "idTaskTimerParameter";

    @Create
    public void create() {
        if (!Boolean.parseBoolean(QUARTZ_PROPERTIES
                .getProperty(QuartzConstant.QUARTZ_TIMER_ENABLED))) {
            return;
        }
        
        final BamTimerManager bamTimerManager = (BamTimerManager) Component.getInstance(BamTimerManager.NAME);
        initProcessoTimerProcessor(bamTimerManager);
        initTarefaTimerProcessor(bamTimerManager);
    }

    private void initTarefaTimerProcessor(final BamTimerManager bamTimerManager) {
        final TarefaTimerProcessor processor = (TarefaTimerProcessor) Component.getInstance(TarefaTimerProcessor.NAME);
        final String cronExpression = QUARTZ_PROPERTIES.getProperty(QuartzConstant.QUARTZ_CRON_EXPRESSION,TAREFA_CRON_EXPRESSION);
        
        initTimerProcessor(cronExpression, ID_INICIAR_TASK_TIMER_PARAMETER, "ID do timer de tarefas do sistema", processor, bamTimerManager);
    }

    private void initProcessoTimerProcessor(final BamTimerManager bamTimerManager) {
        final ProcessoTimerProcessor processor = (ProcessoTimerProcessor) Component.getInstance(ProcessoTimerProcessor.NAME);
        final String cronExpression = QUARTZ_PROPERTIES.getProperty(QuartzConstant.QUARTZ_CRON_EXPRESSION, PROCESSO_CRON_EXPRESSION);
        
        initTimerProcessor(cronExpression, ID_INICIAR_PROCESSO_TIMER_PARAMETER, "ID do timer de projetos do sistema", processor, bamTimerManager);
    }
    
    private void initTimerProcessor(final String cronExpression, final String idTimer, final String description, final BamTimerProcessor processor, final BamTimerManager bamTimerManager) {
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
            LOG.error(e);
        }
    }
    
}
