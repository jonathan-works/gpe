package br.com.infox.epa.service.startup;

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
import org.jbpm.util.ClassLoaderUtil;
import org.quartz.SchedulerException;

import br.com.infox.epa.processor.TarefaTimerProcessor;
import br.com.infox.ibpm.entity.Parametro;
import br.com.infox.timer.TimerUtil;
import br.com.itx.util.EntityUtil;


@Name(TarefaTimerStarter.NAME)
@Scope(ScopeType.APPLICATION)
@Startup(depends="org.jboss.seam.async.dispatcher")
@Install(dependencies={"org.jboss.seam.async.dispatcher"})
public class TarefaTimerStarter {

	private static final String DEFAULT_CRON_EXPRESSION = "0 0/30 * * * ?";
	 
    public static final String NAME = "tarefaTimerStarter";

    public static final String ID_INICIAR_TASK_TIMER_PARAMETER = "idTaskTimerParameter";
    private static Properties quartzProperties = ClassLoaderUtil.getProperties("seam.quartz.properties");
    
    public TarefaTimerStarter(){    	 
    }

    @Create
    @Transactional
    public void init() throws SchedulerException{
        String enabled = quartzProperties.getProperty("org.quartz.timer.enabled", "false");
        if (!"true".equals(enabled)) {
            return;
        }

        String idIniciarFluxoTimer = null; 
		try {
			idIniciarFluxoTimer = TimerUtil.getParametro(ID_INICIAR_TASK_TIMER_PARAMETER);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
        if (idIniciarFluxoTimer == null) {
            Parametro p = new Parametro();
            p.setAtivo(true);
            p.setDescricaoVariavel("ID do timer do sistema");
            p.setDataAtualizacao(new Date());
            p.setNomeVariavel(ID_INICIAR_TASK_TIMER_PARAMETER);
            p.setSistema(true);

            String cronExpression = quartzProperties.getProperty(
           		 "org.quartz.cronExpression", DEFAULT_CRON_EXPRESSION);        
            
            TarefaTimerProcessor processor = TarefaTimerProcessor.instance();
			QuartzTriggerHandle handle = processor.increaseTaskTimeSpent(cronExpression);                     
            EntityUtil.getEntityManager().flush();
            String triggerName = handle.getTrigger().getName();
            p.setValorVariavel(triggerName);
            EntityUtil.getEntityManager().persist(p);
            EntityUtil.getEntityManager().flush();
       }
    }
	
}