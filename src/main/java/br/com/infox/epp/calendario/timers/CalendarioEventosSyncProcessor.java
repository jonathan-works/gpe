package br.com.infox.epp.calendario.timers;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.epp.quartz.client.QuartzRestFactory;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@AutoCreate
@Name(CalendarioEventosSyncProcessor.NAME)
public class CalendarioEventosSyncProcessor {
	
	public static final String NAME = "calendarioEventosSyncProcessor";
	private static final LogProvider LOG = Logging.getLogProvider(CalendarioEventosSyncProcessor.class);
	
	@Asynchronous
	public QuartzTriggerHandle processUpdateCalendarioSync(@IntervalCron String cron) {
	    try {
            QuartzRestFactory.create().processUpdateCalendarioSync();
        } catch (Exception e) {
            LOG.error(e);
        }
	    return null;
	}
	
}
