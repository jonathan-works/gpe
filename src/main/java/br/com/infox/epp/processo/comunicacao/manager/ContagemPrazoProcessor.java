package br.com.infox.epp.processo.comunicacao.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.epp.quartz.client.QuartzRestFactory;

@AutoCreate
@Name(ContagemPrazoProcessor.NAME)
public class ContagemPrazoProcessor {
	
	public static final String NAME = "contagemPrazoProcessor";
	
	@Asynchronous
	public QuartzTriggerHandle processContagemPrazoComunicacao(@IntervalCron String cron) {
        QuartzRestFactory.create().processContagemPrazoComunicacao();
	    return null;
	}
}
