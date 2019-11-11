package br.com.infox.epp.processo.comunicacao.manager;

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
@Name(ContagemPrazoProcessor.NAME)
public class ContagemPrazoProcessor {
	
    @Inject
    protected ParametroManager parametroManager;
    
	public static final String NAME = "contagemPrazoProcessor";
	
	@Asynchronous
	public QuartzTriggerHandle processContagemPrazoComunicacao(@IntervalCron String cron) {
	    
	    if (parametroManager.isNaoExecutarQuartz()) {
            return null;
        }
	    
        QuartzRestFactory.create().processContagemPrazoComunicacao();
	    return null;
	}
}
