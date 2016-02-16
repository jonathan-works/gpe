package br.com.infox.epp.access.timer;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.epp.quartz.client.QuartzRestFactory;

@AutoCreate
@Name(BloqueioUsuarioProcessor.NAME)
public class BloqueioUsuarioProcessor {

    public static final String NAME = "bloqueioUsuarioProcessor";
    
    @Asynchronous
    public QuartzTriggerHandle processBloqueioUsuario(@IntervalCron String cron) {
        QuartzRestFactory.create().processBloqueioUsuario();
        return null;
    }
}
