package br.com.infox.epp.calendario.timers;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.epp.quartz.client.QuartzRestFactory;

@AutoCreate
@Name(CalendarioEventosSyncProcessor.NAME)
public class CalendarioEventosSyncProcessor {
	
	public static final String NAME = "calendarioEventosSyncProcessor";
	
	@Asynchronous
	public QuartzTriggerHandle processUpdateCalendarioSync(@IntervalCron String cron) {
        QuartzRestFactory.create().processUpdateCalendarioSync();
	    return null;
	}
	
}
