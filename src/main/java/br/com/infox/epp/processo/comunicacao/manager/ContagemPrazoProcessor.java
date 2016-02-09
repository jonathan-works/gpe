package br.com.infox.epp.processo.comunicacao.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.epp.quartz.client.QuartzRestFactory;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@AutoCreate
@Name(ContagemPrazoProcessor.NAME)
public class ContagemPrazoProcessor {
	
	public static final String NAME = "contagemPrazoProcessor";
	private static final LogProvider LOG = Logging.getLogProvider(ContagemPrazoProcessor.class);
	
	@Asynchronous
	public QuartzTriggerHandle processContagemPrazoComunicacao(@IntervalCron String cron) {
	    try {
            QuartzRestFactory.create().processContagemPrazoComunicacao();
        } catch (Exception e) {
            LOG.error(e);
        }
	    return null;
	}
}
