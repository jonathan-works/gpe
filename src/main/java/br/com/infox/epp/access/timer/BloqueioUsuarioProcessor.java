package br.com.infox.epp.access.timer;

import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.BloqueioUsuario;
import br.com.infox.epp.access.manager.BloqueioUsuarioManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(BloqueioUsuarioProcessor.NAME)
@AutoCreate
public class BloqueioUsuarioProcessor {

    public static final String NAME = "bloqueioUsuarioProcessor";
    private static final LogProvider LOG = Logging.getLogProvider(BloqueioUsuarioProcessor.class);

    @In
    private BloqueioUsuarioManager bloqueioUsuarioManager;

    @Asynchronous
    @Transactional
    public QuartzTriggerHandle processBloqueioUsuario(@IntervalCron String cron) {

        List<BloqueioUsuario> bloqueios = bloqueioUsuarioManager
                .getBloqueiosAtivos();
        for (BloqueioUsuario bloqueio : bloqueios) {
            Date hoje = new Date();
            Date dataDesbloqueio = bloqueio.getDataPrevisaoDesbloqueio();
            if (dataDesbloqueio.before(hoje)) {
                try {
                    bloqueioUsuarioManager.desfazerBloqueioUsuario(bloqueio
                            .getUsuario());
                } catch (DAOException e) {
                    LOG.error("bloqueioUsuarioProcessor.processBloqueioUsuario()", e);
                }
            }
        }

        return null;
    }
}
