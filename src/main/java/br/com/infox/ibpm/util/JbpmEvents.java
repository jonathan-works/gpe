package br.com.infox.ibpm.util;

import org.jboss.seam.core.Events;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.transaction.UserTransaction;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.exe.ExecutionContext;

public class JbpmEvents {

	public static void raiseEvent(ExecutionContext context) {

		Event event = context.getEvent();
		boolean isJobThread = context.getJbpmContext().getJbpmConfiguration()
				.getJobExecutor().getThreads().containsValue(
						Thread.currentThread());
		if (isJobThread) {
			UserTransaction ut = Transaction.instance();
			boolean completed = false;
			try {
				if (!ut.isActive()) {
					ut.begin();
					try {
						Events.instance().raiseEvent(event.getEventType(), context);
						completed = true;
					} catch (Exception e ){
						e.printStackTrace();
					} finally {
						if (completed) {
							ut.commit();
						} else {
							ut.rollback();
						}
					}
				} else {
					Events.instance().raiseEvent(event.getEventType(), context);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Events.instance().raiseEvent(event.getEventType(), context);
		}
	}

}