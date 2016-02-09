package br.com.infox.epp.access.timer;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.epp.quartz.client.QuartzRestFactory;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@AutoCreate
@Name(BloqueioUsuarioProcessor.NAME)
public class BloqueioUsuarioProcessor {

    public static final String NAME = "bloqueioUsuarioProcessor";
    private static final LogProvider LOG = Logging.getLogProvider(BloqueioUsuarioProcessor.class);
    
    @Asynchronous
    public QuartzTriggerHandle processBloqueioUsuario(@IntervalCron String cron) {
        try {
            QuartzRestFactory.create().processBloqueioUsuario();
        } catch (Exception e) {
            LOG.error(e);
        }
        return null;
    }
}
