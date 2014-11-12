package br.com.infox.epp.processo.comunicacao.timer;

import java.util.Properties;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.util.ClassLoaderUtil;
import org.quartz.SchedulerException;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.comunicacao.manager.ContabilizarPrazoTimerManager;
import br.com.infox.epp.processo.comunicacao.service.ContabilizarPrazoService;
import br.com.infox.quartz.QuartzConstant;

@Name(ContabilizarPrazoService.NAME)
@Scope(ScopeType.STATELESS)
@AutoCreate
public class ContabilizarPrazoStarter {

    public static final String NAME = "contabilizarPrazoStarter";
    public static final String ID_TIMER_CONTABILIZAR_PRAZO = "idTimerContabilizarPrazo";
    private static final LogProvider LOG = Logging.getLogProvider(ContabilizarPrazoStarter.class);
    private static final String DEFAULT_CRON_EXPRESSION = "0 0 0 * * ?";
    
    private static final Properties QUARTZ_PROPERTIES = ClassLoaderUtil.getProperties(QuartzConstant.QUARTZ_PROPERTIES);
    
    public ContabilizarPrazoStarter() {}
    
    @Observer
    @Transactional
    public void init() {
        if (!Boolean.parseBoolean(QUARTZ_PROPERTIES.getProperty(QuartzConstant.QUARTZ_TIMER_ENABLED))) {
            return;
        }
        
        initTimerContabilizarPrazo();
    }

    private void initTimerContabilizarPrazo() {
        ContabilizarPrazoTimerManager manager = (ContabilizarPrazoTimerManager) Component.getInstance(ContabilizarPrazoTimerManager.NAME);
        ContabilizarPrazoProcessor processor = (ContabilizarPrazoProcessor) Component.getInstance(ContabilizarPrazoProcessor.NAME);
        
        try {
            String idContabilizarPrazoTimer = null;
            try {
                idContabilizarPrazoTimer = manager.getParametro(ID_TIMER_CONTABILIZAR_PRAZO);
            } catch (IllegalArgumentException e) {
                LOG.error("ContabilizarPrazoStarter.init()", e);
            }
            if (idContabilizarPrazoTimer == null) {
                manager.createTimerInstance(DEFAULT_CRON_EXPRESSION, ID_TIMER_CONTABILIZAR_PRAZO, "ID do Timer de verificação de prazos das comunicações", processor);
            }
        } catch (SchedulerException | DAOException e) {
            LOG.error(".initTimerContabilizarPrazo", e);
        }
    }
}
