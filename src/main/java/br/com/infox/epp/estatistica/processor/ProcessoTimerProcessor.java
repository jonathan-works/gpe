package br.com.infox.epp.estatistica.processor;

import javax.inject.Inject;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.core.report.RequestInternalPageService;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.estatistica.abstracts.BamTimerProcessor;
import br.com.infox.epp.estatistica.startup.BamTimerStarter;
import br.com.infox.epp.quartz.ws.QuartzRest;
import br.com.infox.epp.tarefa.type.PrazoEnum;
import br.com.infox.epp.ws.client.RestClient;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@AutoCreate
@ContextDependency
@Name(ProcessoTimerProcessor.NAME)
public class ProcessoTimerProcessor implements BamTimerProcessor {

    public static final String NAME = "processoTimerProcessor";
    private static final LogProvider LOG = Logging.getLogProvider(TarefaTimerProcessor.class);
    
    @Inject
    private RequestInternalPageService requestInternalPageService;

    @Asynchronous
    public QuartzTriggerHandle increaseTimeSpent(@IntervalCron String cron) {
        try {
            String key = requestInternalPageService.getKey().toString();
            QuartzRest quartzRest = RestClient.constructInternal(QuartzRest.class);
            quartzRest.getBamResource(key).processoTimerProcessor(BamTimerStarter.ID_INICIAR_PROCESSO_TIMER_PARAMETER, PrazoEnum.D);
        } catch (Exception e) {
            LOG.error(e);
        }
        return null;
    }
    
}
