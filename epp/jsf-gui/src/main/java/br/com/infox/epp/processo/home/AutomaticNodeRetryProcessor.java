package br.com.infox.epp.processo.home;

import javax.inject.Inject;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.quartz.client.QuartzRestFactory;
import br.com.infox.epp.system.manager.ParametroManager;

@ContextDependency
@AutoCreate
@Name(AutomaticNodeRetryProcessor.NAME)
public class AutomaticNodeRetryProcessor {
    
    @Inject
    protected ParametroManager parametroManager;
    
    public static final String NAME = "automaticNodeRetryProcessor";
    
    @Asynchronous
    public QuartzTriggerHandle retryAutomaticNodes(@IntervalCron String cron) {
        
        if (parametroManager.isNaoExecutarQuartz()) {
            return null;
        }
        
        QuartzRestFactory.create().retryAutomaticNodes();
        return null;
    }
}
