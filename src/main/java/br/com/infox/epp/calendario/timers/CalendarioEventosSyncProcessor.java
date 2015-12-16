package br.com.infox.epp.calendario.timers;

import javax.inject.Inject;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.transaction.Transaction;

import br.com.infox.epp.calendario.CalendarioEventosService;
import br.com.infox.epp.cdi.seam.ContextDependency;

@AutoCreate
@Name(CalendarioEventosSyncProcessor.NAME)
@ContextDependency
public class CalendarioEventosSyncProcessor {
	
	public static final String NAME = "calendarioEventosSyncProcessor";
	
	@Inject
	private CalendarioEventosService calendarioEventosService;
	
	@Asynchronous
	public QuartzTriggerHandle processUpdateCalendarioSync(@IntervalCron String cron) {
		UserTransaction transaction = Transaction.instance();
		try {
			transaction.setTransactionTimeout(30000);
			transaction.begin();
			calendarioEventosService.atualizarSeries();
			calendarioEventosService.removeOrphanSeries();
			transaction.commit();
		} catch (Exception e) {
			try {
				transaction.rollback();
			} catch (IllegalStateException | SecurityException | SystemException e1) {
				throw new RuntimeException(e1);
			}
			throw new RuntimeException(e);
		}
	    return null;
	}
	
}
